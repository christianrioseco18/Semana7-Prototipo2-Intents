package com.devst.semana7;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    // Botones
    Button btnLinterna;
    Button btnSegundaVista;
    Button btnUbicacion;
    Button btnMapa;

    // TextView para mostrar ubicación
    TextView txtUbicacion;

    // Variables de la linterna
    CameraManager cameraManager;
    String idCamara;
    boolean linternaEncendida = false;

    // Variables de ubicación
    LocationManager locationManager;
    double latitud = 0;
    double longitud = 0;
    boolean ubicacionObtenida = false;

    // Códigos para permisos
    final int PERMISO_UBICACION = 100;
    final int PERMISO_CAMARA = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // =========================================
        // CONECTAR COMPONENTES DEL XML
        // =========================================

        btnLinterna = findViewById(R.id.btnLinterna);
        btnSegundaVista = findViewById(R.id.btnSegundaVista);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        btnMapa = findViewById(R.id.btnMapa);

        txtUbicacion = findViewById(R.id.txtUbicacion);


        // =========================================
        // PREPARAR LA LINTERNA
        // =========================================

        cameraManager =
                (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {

            String[] camaras =
                    cameraManager.getCameraIdList();

            if (camaras.length > 0) {

                idCamara = camaras[0];

            }

        } catch (CameraAccessException e) {

            Toast.makeText(
                    MainActivity.this,
                    "Error al acceder a la cámara",
                    Toast.LENGTH_SHORT
            ).show();

        }


        // =========================================
        // BOTÓN LINTERNA
        // =========================================

        btnLinterna.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // Revisamos permiso de cámara
                        if (ActivityCompat.checkSelfPermission(
                                MainActivity.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {

                            // Pedimos permiso
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{
                                            Manifest.permission.CAMERA
                                    },
                                    PERMISO_CAMARA
                            );

                            return;
                        }

                        cambiarLinterna();

                    }
                });


        // =========================================
        // INTENT EXPLÍCITO
        // IR A OTRA PANTALLA
        // =========================================

        btnSegundaVista.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        /*
                         * INTENT EXPLÍCITO
                         *
                         * Le indicamos exactamente
                         * qué Activity queremos abrir.
                         */

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        SegundaActivity.class
                                );

                        startActivity(intent);

                    }
                });


        // =========================================
        // OBTENER GEOLOCALIZACIÓN
        // =========================================

        btnUbicacion.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        obtenerUbicacion();

                    }
                });


        // =========================================
        // INTENT IMPLÍCITO
        // ABRIR UBICACIÓN EN MAPA
        // =========================================

        btnMapa.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        abrirMapa();

                    }
                });

    }


    // =============================================
    // ENCENDER / APAGAR LINTERNA
    // =============================================

    private void cambiarLinterna() {

        if (idCamara == null) {

            Toast.makeText(
                    MainActivity.this,
                    "No se encontró una cámara con flash",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            // Cambiamos el estado
            linternaEncendida = !linternaEncendida;

            // Encendemos o apagamos
            cameraManager.setTorchMode(
                    idCamara,
                    linternaEncendida
            );


            // Cambiar texto del botón

            if (linternaEncendida) {

                btnLinterna.setText(
                        "APAGAR LINTERNA"
                );

            } else {

                btnLinterna.setText(
                        "ENCENDER LINTERNA"
                );

            }

        } catch (CameraAccessException e) {

            Toast.makeText(
                    MainActivity.this,
                    "No se pudo controlar la linterna",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }


    // =============================================
    // OBTENER UBICACIÓN
    // =============================================

    private void obtenerUbicacion() {

        locationManager =
                (LocationManager)
                        getSystemService(
                                Context.LOCATION_SERVICE
                        );


        // Revisamos permiso
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // Pedimos permiso
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISO_UBICACION
            );

            return;
        }


        // Revisamos si el GPS está encendido
        if (!locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER)) {

            Toast.makeText(
                    MainActivity.this,
                    "Debes activar el GPS",
                    Toast.LENGTH_LONG
            ).show();


            /*
             * INTENT IMPLÍCITO
             *
             * Android abre la pantalla
             * de configuración correspondiente.
             */

            Intent intent =
                    new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    );

            startActivity(intent);

            return;
        }


        txtUbicacion.setText(
                "Buscando ubicación..."
        );


        // Pedimos una ubicación
        locationManager.requestLocationUpdates(

                LocationManager.GPS_PROVIDER,

                1000,

                1,

                new LocationListener() {

                    @Override
                    public void onLocationChanged(
                            @NonNull Location location) {

                        // Guardamos los datos
                        latitud =
                                location.getLatitude();

                        longitud =
                                location.getLongitude();

                        ubicacionObtenida = true;


                        // Mostramos la ubicación
                        txtUbicacion.setText(

                                "Latitud: "
                                        + latitud
                                        +
                                        "\nLongitud: "
                                        + longitud

                        );


                        Toast.makeText(
                                MainActivity.this,
                                "Ubicación obtenida",
                                Toast.LENGTH_SHORT
                        ).show();


                        // Dejamos de pedir actualizaciones
                        locationManager.removeUpdates(this);

                    }
                }
        );

    }


    // =============================================
    // ABRIR MAPA
    // =============================================

    private void abrirMapa() {

        // Revisamos si tenemos ubicación
        if (!ubicacionObtenida) {

            Toast.makeText(
                    MainActivity.this,
                    "Primero obtén tu ubicación",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Creamos la dirección para el mapa
        String direccion =

                "geo:"
                        + latitud
                        + ","
                        + longitud
                        + "?q="
                        + latitud
                        + ","
                        + longitud;


        Uri uri =
                Uri.parse(direccion);


        /*
         * INTENT IMPLÍCITO
         *
         * No decimos qué aplicación abrir.
         *
         * Android busca una aplicación
         * capaz de mostrar mapas.
         */

        Intent intent =
                new Intent(
                        Intent.ACTION_VIEW,
                        uri
                );


        // Revisamos si existe una aplicación
        // que pueda realizar la acción

        if (intent.resolveActivity(
                getPackageManager()) != null) {

            startActivity(intent);

        } else {

            Toast.makeText(
                    MainActivity.this,
                    "No existe una aplicación de mapas instalada",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }


    // =============================================
    // RESPUESTA DE LOS PERMISOS
    // =============================================

    @Override
    public void onRequestPermissionsResult(

            int requestCode,

            @NonNull String[] permissions,

            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );


        // =========================================
        // PERMISO DE UBICACIÓN
        // =========================================

        if (requestCode == PERMISO_UBICACION) {

            if (grantResults.length > 0
                    &&
                    grantResults[0]
                            == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(
                        MainActivity.this,
                        "Permiso de ubicación aceptado",
                        Toast.LENGTH_SHORT
                ).show();

                obtenerUbicacion();

            } else {

                Toast.makeText(
                        MainActivity.this,
                        "Permiso de ubicación rechazado",
                        Toast.LENGTH_SHORT
                ).show();

            }

        }


        // =========================================
        // PERMISO DE CÁMARA
        // =========================================

        if (requestCode == PERMISO_CAMARA) {

            if (grantResults.length > 0
                    &&
                    grantResults[0]
                            == PackageManager.PERMISSION_GRANTED) {

                cambiarLinterna();

            } else {

                Toast.makeText(
                        MainActivity.this,
                        "Permiso de cámara rechazado",
                        Toast.LENGTH_SHORT
                ).show();

            }

        }

    }

}