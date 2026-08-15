package com.example.appinventario;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity {

    private TextView tvVolverLogin;
    private Button btnRegistrarme;
    private com.google.android.material.textfield.TextInputEditText etRegistroCorreo, etRegistroPassword, etRegistroConfirmacion;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        tvVolverLogin = findViewById(R.id.tvVolverLogin);
        btnRegistrarme = findViewById(R.id.btnRegistrarme);
        etRegistroCorreo = findViewById(R.id.etRegistroCorreo);
        etRegistroPassword = findViewById(R.id.etRegistroPassword);
        etRegistroConfirmacion = findViewById(R.id.etRegistroConfirmacion);

        mAuth = FirebaseAuth.getInstance();

        tvVolverLogin.setOnClickListener(v -> {
            finish();
        });

        btnRegistrarme.setOnClickListener(v -> {
            crearCuentaFirebase();
        });
    }

    private void crearCuentaFirebase(){
        String correo = etRegistroCorreo.getText().toString().trim();
        String password = etRegistroPassword.getText().toString().trim();
        String ConPassword = etRegistroConfirmacion.getText().toString().trim();

        if (correo.isEmpty() || password.isEmpty() || ConPassword.isEmpty()){
            Toast.makeText(RegistroActivity.this, "Por favor llenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(RegistroActivity.this, "La contraseña debe tener minimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(ConPassword)){
            Toast.makeText(RegistroActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistrarme.setEnabled(false);
        btnRegistrarme.setText("CREANDO CUENTA...");

        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(task -> {
                    btnRegistrarme.setEnabled(true);
                    btnRegistrarme.setText("REGISTRARME");

                    if (task.isSuccessful()){
                        Toast.makeText(RegistroActivity.this, "Cuenta creada existosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegistroActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}