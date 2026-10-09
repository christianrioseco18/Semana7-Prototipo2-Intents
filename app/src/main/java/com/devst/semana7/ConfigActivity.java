package com.devst.semana7;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;
/** Valida y guarda los datos que usan los intents externos. */
public class ConfigActivity extends BaseActivity {
    private EditText nombre, web, telefono, correo;
    @Override protected void onCreate(Bundle estado) {
        super.onCreate(estado); setContentView(R.layout.activity_config); prepararVista();
        nombre = findViewById(R.id.edtNombre); web = findViewById(R.id.edtWeb);
        telefono = findViewById(R.id.edtTelefono); correo = findViewById(R.id.edtCorreo);
        SharedPreferences p = getSharedPreferences("ajustes", MODE_PRIVATE);
        if (estado == null) {
            nombre.setText(p.getString("nombre", "Christian")); web.setText(p.getString("web", "https://www.santotomas.cl"));
            telefono.setText(p.getString("telefono", "123456789")); correo.setText(p.getString("correo", "estudiante@example.com"));
        }
        findViewById(R.id.btnGuardar).setOnClickListener(v -> guardar());
    }
    private void guardar() {
        String n = nombre.getText().toString().trim(), w = web.getText().toString().trim();
        String t = telefono.getText().toString().trim(), c = correo.getText().toString().trim();
        nombre.setError(null); web.setError(null); telefono.setError(null); correo.setError(null);
        boolean valido = true;
        if (n.isEmpty() || n.length() > 60) { nombre.setError("Ingresa un nombre de 1 a 60 caracteres"); valido = false; }
        Uri uri = Uri.parse(w);
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null || !Patterns.WEB_URL.matcher(w).matches()) {
            web.setError("Ingresa una dirección HTTPS válida"); valido = false;
        }
        if (!t.matches("[+]?[0-9]{3,15}")) { telefono.setError("Usa entre 3 y 15 dígitos, con + opcional"); valido = false; }
        if (!Patterns.EMAIL_ADDRESS.matcher(c).matches()) { correo.setError("Ingresa un correo válido"); valido = false; }
        if (!valido) return;
        getSharedPreferences("ajustes", MODE_PRIVATE).edit().putString("nombre", n).putString("web", w)
            .putString("telefono", t).putString("correo", c).apply();
        Toast.makeText(this, "Ajustes guardados", Toast.LENGTH_SHORT).show(); finish();
    }
}
