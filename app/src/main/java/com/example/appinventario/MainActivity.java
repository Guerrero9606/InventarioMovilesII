package com.example.appinventario;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private com.google.android.material.button.MaterialButton btnCrearArticulo, btnBuscar, btnEditar, btnBorrar, btnBuscarTodos, btnFiltrar, btnCerrarSesion;
    private EditText etCodigo, etDescripcion, etPrecio, etNombreTienda;
    private Button btnGuardarTienda;
    private com.google.android.material.textfield.TextInputLayout tilCodigo, tilDescripcion, tilPrecio;
    private RecyclerView rvProductos;
    private AdaptadorProducto adaptador;
    private List<Producto> listaProductos;
    private FirebaseFirestore db;
    private com.google.firebase.firestore.ListenerRegistration listenerFirestore;
    private com.google.android.material.switchmaterial.SwitchMaterial swOferta;
    private android.widget.ProgressBar pbCarga;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCodigo = findViewById(R.id.etCodigo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        etNombreTienda = findViewById(R.id.etNombreTienda);

        btnCrearArticulo = findViewById(R.id.btnCrearArticulo);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        //btnBuscarTodos = findViewById(R.id.btnBuscarTodos);
        btnFiltrar = findViewById(R.id.btnFiltrar);
        rvProductos = findViewById(R.id.rvProductos);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnGuardarTienda = findViewById(R.id.btnGuardarTienda);

        tilCodigo = findViewById(R.id.tilCodigo);
        tilDescripcion = findViewById(R.id.tilDescripcion);
        tilPrecio = findViewById(R.id.tilPrecio);

        swOferta = findViewById(R.id.swOferta);
        pbCarga = findViewById(R.id.pbCarga);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        rvProductos.setLayoutManager(new LinearLayoutManager(this));

        /*btnBuscarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarListaProductos();
            }
        });*/

        etPrecio.addTextChangedListener(new android.text.TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count){
                if (sequence.toString().isEmpty()){
                    tilPrecio.setError("El precio no puede estar vacio");
                } else if (Double.parseDouble(sequence.toString()) <= 0){
                    tilPrecio.setError("El precio debe ser mayor a cero");
                } else {
                    tilPrecio.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable sequence) {}
        });

        btnCrearArticulo.setOnClickListener(v -> {
            if (esFormularioValido()) {
                registrarProductoFirebase();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProductoFirebase();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { actualizarProductoFirebase(); }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { eliminarProductoFirebase(); }
        });

        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { filtrarSoloOfertas(); }
        });

        btnCerrarSesion.setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("SesionUsuario", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnGuardarTienda.setOnClickListener(v->{
            String nombreTienda = etNombreTienda.getText().toString().trim();

            if (!nombreTienda.isEmpty()){
                SharedPreferences preferencias = getSharedPreferences("ConfiguracionApp", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferencias.edit();

                editor.putString("nombre_tienda", nombreTienda);
                editor.apply();

                Toast.makeText(MainActivity.this, "Nombre guardado en memoria", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Ingrese el nombre de la tienda", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences prefs = getSharedPreferences("SesionUsuario", MODE_PRIVATE);
        String nombreCached = prefs.getString("nombre", "Usuario");
        String rolCached = prefs.getString("rol", "Vendedor");

        SharedPreferences preferencias = getSharedPreferences("ConfiguracionApp", MODE_PRIVATE);
        String tiendaGuardada = preferencias.getString("nombre_tienda", "Mi Inventario");

        etNombreTienda.setText(tiendaGuardada);

        cargarProductosFirebaseTiempoReal();
    }

    private boolean esFormularioValido(){
        boolean esValido = true;

        String codigo = etCodigo.getText().toString();
        String desc = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        if (codigo.isEmpty() || codigo.length() < 3) {
            tilCodigo.setError("El codigo debe tener al menos 3 digitos");
            esValido = false;
        } else {
            tilCodigo.setErrorEnabled(false);
        }

        if (desc.isEmpty() || desc.length() < 10) {
            tilDescripcion.setError("Sea mas descriptivo con el producto (min. 10 caracteres)");
            esValido = false;
        } else {
            tilDescripcion.setErrorEnabled(false);
        }

        return esValido;
    }
    private void registrarProducto(){
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase(); //.getReadableDatabase() Abre la base de datos en solo lectura, getWritableDatabase() abre la base de datos en lectura y escritura.

            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("precio", precio);

            db.insert("articulos", null, registro);

            //Cerrar conexion a la base de datos por seguridad y por consumo de recursos.
            db.close();

            cargarListaProductos();

            if (adaptador != null){
                adaptador.notifyDataSetChanged();
            }

            etCodigo.setText("");
            etDescripcion.setText("");
            etPrecio.setText("");

            Toast.makeText(MainActivity.this, "Articulo registrado correctamente en base de datos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Todos los campos deben estar diligenciados", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarProducto(){
        String codigo = etCodigo.getText().toString();

        if (!codigo.isEmpty()){
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase db = admin.getReadableDatabase();

            android.database.Cursor fila = db.rawQuery("SELECT descripcion, precio FROM articulos WHERE codigo = " + codigo, null);

            if (fila.moveToFirst()){
                etDescripcion.setText(fila.getString(0));
                etPrecio.setText(fila.getString(1));
                Toast.makeText(this, "Producto encontrado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El producto no existe", Toast.LENGTH_SHORT).show();
                etCodigo.setText("");
                etDescripcion.setText("");
                etPrecio.setText("");
            }

            db.close();
            fila.close();

        } else {
            Toast.makeText(this, "Ingrese el codigo del producto a buscar", Toast.LENGTH_SHORT).show();
        }
    }

    private void editarProducto(){
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            ContentValues registroNuevo = new ContentValues();
            registroNuevo.put("codigo", codigo);
            registroNuevo.put("descripcion", descripcion);
            registroNuevo.put("precio", precio);

            int cantidadActualizadas = db.update("articulos", registroNuevo, "codigo="+codigo, null);

            db.close();

            if (cantidadActualizadas == 1){
                Toast.makeText(MainActivity.this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "No se encontro el producto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void borrarProducto(){
        String codigo = etCodigo.getText().toString();

        if (!codigo.isEmpty()){
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            int cantidadBorrados = db.delete("articulos", "codigo="+codigo,null);

            db.close();

            etCodigo.setText("");
            etDescripcion.setText("");
            etPrecio.setText("");

            if (cantidadBorrados == 1){
                Toast.makeText(MainActivity.this, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Ingrese el codigo del producto a eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarListaProductos(){
        /*listaProductos = new ArrayList<>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        android.database.Cursor fila = db.rawQuery("SELECT codigo, descripcion, precio FROM articulos", null);

        while (fila.moveToNext()){
            int codigo = fila.getInt(0);
            String descripcion = fila.getString(1);
            double precio = fila.getDouble(2);

            listaProductos.add(new Producto(codigo, descripcion, precio));
        }

        db.close();
        fila.close();

        adaptador = new AdaptadorProducto(listaProductos);

        rvProductos.setAdapter(adaptador);*/
    }

    private void registrarProductoFirebase(){
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        boolean estaEnOferta = swOferta.isChecked();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){

            pbCarga.setVisibility(View.VISIBLE);
            btnCrearArticulo.setEnabled(false);

            Map<String, Object> productoMap = new HashMap<>();
            productoMap.put("codigo", Integer.parseInt(codigo));
            productoMap.put("descripcion", descripcion);
            productoMap.put("precio", Double.parseDouble(precio));
            productoMap.put("oferta", estaEnOferta);

            db.collection("productos").document(codigo)
                .set(productoMap)
                .addOnSuccessListener(aVoid -> {
                    pbCarga.setVisibility(View.GONE);
                    btnCrearArticulo.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Guardado en Firebase con exito", Toast.LENGTH_SHORT).show();
                    etCodigo.setText("");
                    etDescripcion.setText("");
                    etPrecio.setText("");
                    swOferta.setChecked(false);
                })
                .addOnFailureListener(e -> {
                    pbCarga.setVisibility(View.GONE);
                    btnCrearArticulo.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        } else {
            Toast.makeText(MainActivity.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarProductosFirebase(){
        listaProductos = new ArrayList<>();

        db.collection("productos").get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    listaProductos.clear();

                    for (QueryDocumentSnapshot document : task.getResult()){
                        Producto producto = document.toObject(Producto.class);
                        listaProductos.add(producto);
                    }

                    adaptador = new AdaptadorProducto(listaProductos);
                    rvProductos.setAdapter(adaptador);
                } else {
                    Toast.makeText(MainActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void cargarProductosFirebaseTiempoReal(){
        listaProductos = new ArrayList<>();
        adaptador = new AdaptadorProducto(listaProductos);
        rvProductos.setAdapter(adaptador);

        listenerFirestore = db.collection("productos")
            .addSnapshotListener((value, error) -> {
                if (error != null){
                    Toast.makeText(MainActivity.this, "Fallo al escuchar los cambios", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    listaProductos.clear();

                    for (QueryDocumentSnapshot documento : value){
                        Producto producto = documento.toObject(Producto.class);
                        listaProductos.add(producto);
                    }

                    adaptador.notifyDataSetChanged();
                }
            });
    }

    private void filtrarSoloOfertas(){
        db.collection("productos")
            .whereEqualTo("oferta", true)
            .whereLessThan("precio", 1000000)
            .addSnapshotListener((value, error) -> {
                if (error != null){
                    return;
                }

                if (value != null){
                    listaProductos.clear();
                    for (QueryDocumentSnapshot doc : value){
                        listaProductos.add(doc.toObject(Producto.class));
                    }
                    adaptador.notifyDataSetChanged();
                }
            });
    }

    private void buscarProductoFirebase(){
        String codigo = etCodigo.getText().toString();

        if (codigo.isEmpty()){
            Toast.makeText(MainActivity.this, "Ingrese el codigo a buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("productos").document(codigo).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()){
                    String descripcion = documentSnapshot.getString("descripcion");
                    Double precio = documentSnapshot.getDouble("precio");
                    Boolean oferta = documentSnapshot.getBoolean("oferta");

                    etDescripcion.setText(descripcion);
                    etPrecio.setText(String.valueOf(precio));

                    if (oferta != null){
                        swOferta.setChecked(oferta);
                    } else {
                        swOferta.setChecked(false);
                    }

                    Toast.makeText(MainActivity.this, "Producto encontrado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "El producto no existe", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
               Toast.makeText(MainActivity.this, "Error de conexion", Toast.LENGTH_SHORT).show();
            });
    }

    private void actualizarProductoFirebase(){
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();
        boolean estaEnOferta = swOferta.isChecked();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){
            db.collection("productos").document(codigo)
                .update(
                    "descripcion", descripcion,
                        "precio", Double.parseDouble(precio),
                        "oferta", estaEnOferta
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                    etCodigo.setText("");
                    etDescripcion.setText("");
                    etPrecio.setText("");
                    swOferta.setChecked(false);
                })
                .addOnFailureListener(e -> {
                   Toast.makeText(MainActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                });
        } else {
            Toast.makeText(MainActivity.this, "Llena todos los campos del formulario", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarProductoFirebase(){
        String codigo = etCodigo.getText().toString();

        if (codigo.isEmpty()){
            Toast.makeText(MainActivity.this, "Ingrese el codigo del producto a eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("productos").document(codigo).delete()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(MainActivity.this, "Producto borrado", Toast.LENGTH_SHORT).show();
                etCodigo.setText("");
                etDescripcion.setText("");
                etPrecio.setText("");
                swOferta.setChecked(false);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, "Error al borrar el producto", Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (listenerFirestore != null) {
            listenerFirestore.remove();
        }
    }
}