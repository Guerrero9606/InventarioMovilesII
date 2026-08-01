package com.example.appinventario;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class RegistroActivity extends AppCompatActivity {

    private TextView tvVolverLogin;
    private Button btnRegistrarme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        tvVolverLogin = findViewById(R.id.tvVolverLogin);
        btnRegistrarme = findViewById(R.id.btnRegistrarme);

        tvVolverLogin.setOnClickListener(v -> {
            finish();
        });

        btnRegistrarme.setOnClickListener(v -> {
            Toast.makeText(RegistroActivity.this, "Usuario Registrado", Toast.LENGTH_SHORT).show();
        });
    }
}