package com.example.appinventario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class AdaptadorProducto extends RecyclerView.Adapter<AdaptadorProducto.ProductoViewHolder> {

    private List<Producto> listaProductos;

    public AdaptadorProducto(List<Producto> listaProductos){
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position){
        Producto productoActual = listaProductos.get(position);

        holder.tvCodigo.setText(String.valueOf(productoActual.getCodigo()));
        holder.tvDescripcion.setText(productoActual.getDescripcion());
        holder.tvPrecio.setText("$ " + productoActual.getPrecio());

        if (productoActual.getPrecio() >= 1000000){
            holder.tvEstado.setText("PREMIUM");
            holder.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#FFC107"));
        } else {
            holder.tvEstado.setText("ESTANDAR");
            holder.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Seccionaste: " + productoActual.getDescripcion(), Toast.LENGTH_SHORT ).show();
            }
        });
    }

    @Override
    public int getItemCount(){
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvDescripcion, tvPrecio, tvEstado;

        public ProductoViewHolder(@NonNull View itemView){
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvItemCodigo);
            tvDescripcion = itemView.findViewById(R.id.tvItemDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvItemPrecio);
            tvEstado = itemView.findViewById(R.id.tvItemEstado);
        }
    }

}
