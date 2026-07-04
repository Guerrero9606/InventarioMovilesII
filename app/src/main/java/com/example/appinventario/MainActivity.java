package com.example.appinventario;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnCrearArticulo, btnBuscar, btnEditar, btnBorrar, btnBuscarTodos;
    private EditText etCodigo, etDescripcion, etPrecio;

    private RecyclerView rvProductos;
    private AdaptadorProducto adaptador;
    private List<Producto> listaProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCodigo = findViewById(R.id.etCodigo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        btnCrearArticulo = findViewById(R.id.btnCrearArticulo);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnBuscarTodos = findViewById(R.id.btnBuscarTodos);
        rvProductos = findViewById(R.id.rvProductos);

        rvProductos.setLayoutManager(new LinearLayoutManager(this));

        btnBuscarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarListaProductos();
            }
        });

        btnCrearArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarProducto();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProducto();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { editarProducto(); }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { borrarProducto(); }
        });

        cargarListaProductos();
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

            Toast.makeText(this, "Articulo registrado correctamente en base de datos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Todos los campos deben estar diligenciados", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se encontro el producto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingrese el codigo del producto a eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarListaProductos(){
        listaProductos = new ArrayList<>();

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

        rvProductos.setAdapter(adaptador);
    }
}