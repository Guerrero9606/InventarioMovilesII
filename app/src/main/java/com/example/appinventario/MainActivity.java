package com.example.appinventario;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnCrearDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCrearDB = findViewById(R.id.btnCrearDB);

        btnCrearDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "tienda.db", null, 1);

                admin.getWritableDatabase();

                Toast.makeText(MainActivity.this, "Base de datos creada correctamente", Toast.LENGTH_SHORT).show();

                btnCrearDB.setText("CONECTADA");
                btnCrearDB.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorAcentos));
                //getResources().getColor(), ya no se usa en versiones recientes de Android (API nuevas)
                //La manera moderna es: ContextCompat.getColor(this, R.color.colorAcentos)
            }
        });
    }
}