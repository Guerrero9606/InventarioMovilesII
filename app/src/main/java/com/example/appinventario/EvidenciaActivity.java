package com.example.appinventario;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.security.Permission;

public class EvidenciaActivity extends AppCompatActivity {

    private ImageView ivPreviaFoto;
    private Button btnTomarFoto, btnAbrirGaleria;

    private ActivityResultLauncher<String> solicitarPermisoCamaraLauncher;
    private ActivityResultLauncher<Void> abrirCamaraLauncher;
    private ActivityResultLauncher<String> abrirGaleriaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidencia);

        ivPreviaFoto = findViewById(R.id.ivPreviaFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnAbrirGaleria = findViewById(R.id.btnAbrirGaleria);

        abrirGaleriaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uriResult -> {
                    if (uriResult != null){
                        ivPreviaFoto.setImageURI(uriResult);
                        Toast.makeText(this, "Imagen cargada desde Galeria", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        abrirCamaraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmapResult -> {
                    if (bitmapResult != null){
                        ivPreviaFoto.setImageBitmap(bitmapResult);
                        Toast.makeText(this, "Foto capturada con exito", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        solicitarPermisoCamaraLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted){
                        abrirCamaraLauncher.launch(null);
                    } else {
                        Toast.makeText(this, "Permiso denegado. No se puede usar la camara.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnAbrirGaleria.setOnClickListener(v -> {
            abrirGaleriaLauncher.launch("image/*");
        });

        btnTomarFoto.setOnClickListener(v -> {
            verificarYEjecutarCamara();
        });
    }

    private void verificarYEjecutarCamara(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            abrirCamaraLauncher.launch(null);
        } else {
            solicitarPermisoCamaraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

}