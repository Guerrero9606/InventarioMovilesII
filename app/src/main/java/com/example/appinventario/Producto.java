package com.example.appinventario;

import java.util.List;
public class Producto {
    private int codigo;
    private String descripcion;
    private double precio;
    private boolean oferta;
    private String urlFoto;
    private List<String> fotosEvidencia;

    public Producto () {};
    public Producto (int codigo, String descripcion, double precio, boolean oferta){
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.oferta = oferta;
    }

    public int getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public boolean isOferta() { return oferta; }
    public String getUrlFoto() {return urlFoto;}
    public void setUrlFoto(String urlFoto) {this.urlFoto = urlFoto;}

    public List<String> getFotosEvidencia() { return fotosEvidencia; }
    public void setFotosEvidencia(List<String> fotosEvidencia) {this.fotosEvidencia = fotosEvidencia;}
}
