package com.devst.semana7;
import android.os.Bundle;
import android.widget.TextView;
/** Recibe los extras enviados desde MainActivity. */
public class SegundaActivity extends BaseActivity {
    @Override protected void onCreate(Bundle estado) {
        super.onCreate(estado); setContentView(R.layout.activity_segunda); prepararVista();
        String nombre = getIntent().getStringExtra("nombre");
        String ubicacion = getIntent().getStringExtra("ubicacion");
        if (nombre == null || nombre.trim().isEmpty()) nombre = "Estudiante";
        if (ubicacion == null) ubicacion = "Sin ubicación disponible";
        ((TextView) findViewById(R.id.txtDetalle)).setText("Hola, " + nombre + "\n\n" + ubicacion +
            "\n\nEsta pantalla recibió los datos mediante putExtra y getStringExtra.");
    }
}
