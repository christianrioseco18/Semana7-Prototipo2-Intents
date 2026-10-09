package com.devst.semana7;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Mantiene la base de Semana 7 y agrega cinco intents implícitos y tres explícitos. */
public class MainActivity extends AppCompatActivity {
    private TextView txtUbicacion, txtEstado;
    private ImageView imgGaleria;
    private Button btnLinterna;
    private CameraManager cameraManager;
    private String idCamara;
    private boolean linternaEncendida, ubicacionObtenida;
    private double latitud, longitud;
    private Uri imagenSeleccionada;
    private LocationManager locationManager;
    private LocationListener escuchaUbicacion;
    private final Handler handler = new Handler(Looper.getMainLooper());
    // Decodificar imágenes grandes fuera del hilo principal evita bloquear la interfaz.
    private final ExecutorService hiloImagen = Executors.newSingleThreadExecutor();
    private int solicitudImagen = 0;
    private boolean destruida = false;
    private final Runnable limiteUbicacion = () -> {
        detenerUbicacion();
        txtUbicacion.setText("No se obtuvo ubicación en 25 segundos. Reintenta o usa el mapa de ejemplo.");
    };

    private final ActivityResultLauncher<String[]> permisoUbicacion = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(), resultados -> {
            if (Boolean.TRUE.equals(resultados.get(Manifest.permission.ACCESS_FINE_LOCATION))
                || Boolean.TRUE.equals(resultados.get(Manifest.permission.ACCESS_COARSE_LOCATION))) {
                obtenerUbicacion();
            } else { mensaje("Permiso rechazado. Puedes usar el mapa de ejemplo."); }
        });
    private final ActivityResultLauncher<String> permisoCamara = registerForActivityResult(
        new ActivityResultContracts.RequestPermission(), permitido -> {
            if (permitido) cambiarLinterna(); else mensaje("Permiso de cámara rechazado.");
        });
    // Implícito 5: GetContent crea ACTION_GET_CONTENT con tipo image/* y devuelve una URI.
    private final ActivityResultLauncher<String> galeria = registerForActivityResult(
        new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) { mensaje("Selección cancelada. La imagen anterior se conserva."); return; }
            cargarImagen(uri);
        });

    @Override protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.activity_main);
        // Evita que la interfaz quede debajo de las barras del sistema en Android reciente.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            androidx.core.graphics.Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });
        txtUbicacion = findViewById(R.id.txtUbicacion);
        txtEstado = findViewById(R.id.txtEstado);
        imgGaleria = findViewById(R.id.imgGaleria);
        btnLinterna = findViewById(R.id.btnLinterna);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        prepararLinterna();
        btnLinterna.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                permisoCamara.launch(Manifest.permission.CAMERA);
            else cambiarLinterna();
        });
        findViewById(R.id.btnUbicacion).setOnClickListener(v -> obtenerUbicacion());
        findViewById(R.id.btnMapa).setOnClickListener(v -> abrirMapa());
        findViewById(R.id.btnWeb).setOnClickListener(v -> abrirSeguro(
            new Intent(Intent.ACTION_VIEW, Uri.parse(preferencias().getString("web", "https://www.santotomas.cl")))));
        findViewById(R.id.btnTelefono).setOnClickListener(v -> abrirSeguro(
            new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", preferencias().getString("telefono", "123456789"), null))));
        findViewById(R.id.btnCorreo).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", preferencias().getString("correo", "estudiante@example.com"), null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre el prototipo Android");
            intent.putExtra(Intent.EXTRA_TEXT, "Hola, estoy probando los intents del proyecto Semana 7.");
            abrirSeguro(intent);
        });
        findViewById(R.id.btnGaleria).setOnClickListener(v -> {
            try { galeria.launch("image/*"); }
            catch (ActivityNotFoundException | SecurityException e) { mensaje("No se pudo abrir el selector de imágenes."); }
        });
        // Explícito 1: destino conocido y datos mediante extras.
        findViewById(R.id.btnSegundaVista).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SegundaActivity.class);
            intent.putExtra("nombre", preferencias().getString("nombre", "Christian"));
            intent.putExtra("ubicacion", txtUbicacion.getText().toString());
            startActivity(intent);
        });
        // Explícitos 2 y 3: Activities propias declaradas en AndroidManifest.xml.
        findViewById(R.id.btnConfig).setOnClickListener(v -> startActivity(new Intent(this, ConfigActivity.class)));
        findViewById(R.id.btnAyuda).setOnClickListener(v -> startActivity(new Intent(this, AyudaActivity.class)));
        if (estado != null) {
            ubicacionObtenida = estado.getBoolean("ubicacionObtenida");
            latitud = estado.getDouble("latitud"); longitud = estado.getDouble("longitud");
            if (ubicacionObtenida) mostrarCoordenadas();
            String uri = estado.getString("imagen");
            if (uri != null) cargarImagen(Uri.parse(uri));
        }
    }
    private SharedPreferences preferencias() { return getSharedPreferences("ajustes", MODE_PRIVATE); }
    private void mensaje(String texto) { Toast.makeText(this, texto, Toast.LENGTH_LONG).show(); }
    /** Lanza la acción y maneja la ausencia de una app compatible sin un cierre inesperado. */
    private void abrirSeguro(Intent intent) {
        try { startActivity(intent); }
        catch (ActivityNotFoundException e) { mensaje("No hay una aplicación compatible instalada para esta acción."); }
        catch (SecurityException e) { mensaje("El sistema no permite realizar esta acción."); }
    }
    private void abrirMapa() {
        double lat = ubicacionObtenida ? latitud : -33.4489;
        double lon = ubicacionObtenida ? longitud : -70.6693;
        String etiqueta = ubicacionObtenida ? "Mi ubicación" : "Santiago centro - ubicación de ejemplo";
        if (!ubicacionObtenida) mensaje("Se abrirá Santiago centro como ejemplo, no tu ubicación actual.");
        Uri uri = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + Uri.encode(etiqueta) + ")");
        abrirSeguro(new Intent(Intent.ACTION_VIEW, uri));
    }
    private void prepararLinterna() {
        if (cameraManager != null) {
            try {
                for (String id : cameraManager.getCameraIdList()) {
                    if (Boolean.TRUE.equals(cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE))) {
                        idCamara = id; break;
                    }
                }
            } catch (Exception e) { mensaje("No se pudo consultar el flash."); }
        }
        if (idCamara == null) { btnLinterna.setEnabled(false); btnLinterna.setText("LINTERNA NO DISPONIBLE"); }
    }
    private void cambiarLinterna() {
        if (idCamara == null || cameraManager == null) { mensaje("Este dispositivo no tiene flash disponible."); return; }
        try {
            boolean nuevoEstado = !linternaEncendida;
            cameraManager.setTorchMode(idCamara, nuevoEstado);
            linternaEncendida = nuevoEstado;
            btnLinterna.setText(nuevoEstado ? "APAGAR LINTERNA" : "ENCENDER LINTERNA");
        } catch (Exception e) { mensaje("No se pudo cambiar la linterna. Revisa si otra app usa la cámara."); }
    }
    private boolean tienePermiso(String permiso) {
        return ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED;
    }
    private void obtenerUbicacion() {
        boolean precisa = tienePermiso(Manifest.permission.ACCESS_FINE_LOCATION);
        if (!precisa && !tienePermiso(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permisoUbicacion.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}); return;
        }
        if (locationManager == null) { mensaje("Ubicación no disponible."); return; }
        detenerUbicacion();
        try {
            String proveedor = null;
            if (precisa && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) proveedor = LocationManager.GPS_PROVIDER;
            else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) proveedor = LocationManager.NETWORK_PROVIDER;
            if (proveedor == null) { mensaje("Activa la ubicación en los ajustes del dispositivo y vuelve a intentarlo."); return; }
            txtUbicacion.setText("Buscando ubicación... máximo 25 segundos.");
            escuchaUbicacion = new LocationListener() {
                @Override public void onLocationChanged(@NonNull Location location) {
                    latitud = location.getLatitude(); longitud = location.getLongitude();
                    ubicacionObtenida = true; mostrarCoordenadas(); detenerUbicacion();
                }
            };
            locationManager.requestLocationUpdates(proveedor, 1000, 1, escuchaUbicacion, Looper.getMainLooper());
            handler.postDelayed(limiteUbicacion, 25000);
        } catch (SecurityException | IllegalArgumentException e) {
            detenerUbicacion(); txtUbicacion.setText("No se pudo obtener la ubicación."); mensaje("Revisa los permisos y la ubicación del dispositivo.");
        }
    }
    private void mostrarCoordenadas() { txtUbicacion.setText("Latitud: " + latitud + "\nLongitud: " + longitud); }
    private void detenerUbicacion() {
        handler.removeCallbacks(limiteUbicacion);
        if (locationManager != null && escuchaUbicacion != null) {
            try { locationManager.removeUpdates(escuchaUbicacion); } catch (SecurityException ignored) { }
            escuchaUbicacion = null;
        }
    }
    private void cargarImagen(Uri uri) {
        final int numero = ++solicitudImagen;
        txtEstado.setText("Cargando imagen en un hilo de trabajo...");
        hiloImagen.execute(() -> {
            try {
                BitmapFactory.Options opciones = new BitmapFactory.Options();
                opciones.inJustDecodeBounds = true;
                try (InputStream entrada = getContentResolver().openInputStream(uri)) {
                    if (entrada == null) throw new IllegalArgumentException("URI sin contenido");
                    BitmapFactory.decodeStream(entrada, null, opciones);
                }
                if (opciones.outWidth <= 0 || opciones.outHeight <= 0) throw new IllegalArgumentException("Imagen inválida");
                opciones.inSampleSize = 1;
                while (opciones.outWidth / opciones.inSampleSize > 1200 || opciones.outHeight / opciones.inSampleSize > 1200)
                    opciones.inSampleSize *= 2;
                opciones.inJustDecodeBounds = false;
                Bitmap bitmap;
                try (InputStream entrada = getContentResolver().openInputStream(uri)) {
                    if (entrada == null) throw new IllegalArgumentException("URI sin contenido");
                    bitmap = BitmapFactory.decodeStream(entrada, null, opciones);
                }
                if (bitmap == null) throw new IllegalArgumentException("Imagen ilegible");
                handler.post(() -> {
                    if (destruida || numero != solicitudImagen) { bitmap.recycle(); return; }
                    imagenSeleccionada = uri;
                    imgGaleria.setImageBitmap(bitmap);
                    txtEstado.setText("Imagen recibida mediante URI y mostrada en ImageView.");
                });
            } catch (Exception | OutOfMemoryError e) {
                handler.post(() -> { if (!destruida && numero == solicitudImagen) txtEstado.setText("No se pudo leer la imagen. Selecciona otra."); });
            }
        });
    }
    @Override protected void onSaveInstanceState(@NonNull Bundle estado) {
        super.onSaveInstanceState(estado);
        estado.putBoolean("ubicacionObtenida", ubicacionObtenida);
        estado.putDouble("latitud", latitud); estado.putDouble("longitud", longitud);
        if (imagenSeleccionada != null) estado.putString("imagen", imagenSeleccionada.toString());
    }
    @Override protected void onStop() {
        super.onStop();
        if (escuchaUbicacion != null) { detenerUbicacion(); txtUbicacion.setText("Búsqueda detenida al salir. Puedes volver a obtener tu ubicación."); }
        if (linternaEncendida) cambiarLinterna();
    }
    @Override protected void onDestroy() {
        destruida = true; solicitudImagen++; detenerUbicacion(); hiloImagen.shutdownNow();
        super.onDestroy();
    }
}
