package com.devst.semana7;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
/** Comparte el manejo de barras del sistema y el botón Volver entre las vistas internas. */
public abstract class BaseActivity extends AppCompatActivity {
    protected void prepararVista() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            androidx.core.graphics.Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom); return insets;
        });
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }
}
