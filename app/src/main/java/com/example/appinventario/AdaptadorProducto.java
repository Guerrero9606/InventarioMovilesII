package com.example.appinventario;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class AdaptadorProducto extends RecyclerView.Adapter<AdaptadorProducto.ProductoViewHolder> {

    private List<Producto> listaProductos;
    private String rolUsuario;

    public AdaptadorProducto(List<Producto> listaProductos, String rolUsuario){
        this.listaProductos = listaProductos;
        this.rolUsuario = rolUsuario;
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
        List<String> fotos = productoActual.getFotosEvidencia();

        holder.tvCodigo.setText(String.valueOf(productoActual.getCodigo()));
        holder.tvDescripcion.setText(productoActual.getDescripcion());
        holder.tvPrecio.setText("$ " + productoActual.getPrecio());

        String urlImagen = productoActual.getUrlFoto();

        if(urlImagen != null && !urlImagen.isEmpty()){

            Glide.with(holder.itemView.getContext())
                    .load(urlImagen)
                    .centerCrop()
                    .error(android.R.drawable.stat_notify_error)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("GLIDE_DEBUG", "Fallo al cargar: " + (e != null ? e.getMessage() : "Desconocido"));
                            return false; // Permite que se dibuje el drawable de .error()
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("GLIDE_DEBUG", "Imagen cargada con éxito desde: " + dataSource); // Puede ser MEMORY, DISK o NETWORK
                            return false;
                        }
                    })
                    .into(holder.ivItemFoto);
        } else {
            Glide.with(holder.itemView.getContext()).clear(holder.ivItemFoto);
            holder.ivItemFoto.setImageResource(R.drawable.ic_caja);
        }

        if (fotos != null && !fotos.isEmpty()){
            String fotoPortada = fotos.get(0);

            holder.tvBadgeFoto.setVisibility(View.VISIBLE);
            holder.tvBadgeFoto.setText("📷 " + fotos.size() + " fotos");

            Glide.with(holder.itemView.getContext())
                    .load(fotoPortada)
                    .centerCrop()
                    .placeholder(R.drawable.ic_caja)
                    .into(holder.ivItemFoto);
        } else {
            Glide.with(holder.itemView.getContext()).clear(holder.ivItemFoto);
            holder.ivItemFoto.setImageResource(R.drawable.ic_caja);
            holder.tvBadgeFoto.setVisibility(View.GONE);
        }

        /*if (productoActual.getPrecio() >= 1000000){
            holder.tvEstado.setText("PREMIUM");
            holder.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#FFC107"));
        } else {
            holder.tvEstado.setText("ESTANDAR");
            holder.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"));
        }*/

        if (rolUsuario.equals("Vendedor")){
            holder.itemView.setClickable(false);
            holder.itemView.setFocusable(false);
        } else {
            holder.itemView.setClickable(true);

            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Seccionaste: " + productoActual.getDescripcion(), Toast.LENGTH_SHORT ).show();
            });
        }

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Seccionaste: " + productoActual.getDescripcion(), Toast.LENGTH_SHORT ).show();
            }
        });*/
    }

    @Override
    public int getItemCount(){
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvDescripcion, tvPrecio, tvEstado, tvBadgeFoto;
        ImageView ivItemFoto;

        public ProductoViewHolder(@NonNull View itemView){
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvItemCodigo);
            tvDescripcion = itemView.findViewById(R.id.tvItemDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvItemPrecio);
            tvEstado = itemView.findViewById(R.id.tvItemEstado);
            ivItemFoto = itemView.findViewById(R.id.ivItemFoto);
            tvBadgeFoto = itemView.findViewById(R.id.tvBadgeFoto);
        }
    }

}
