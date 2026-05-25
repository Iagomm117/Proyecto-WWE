package model;

import java.io.Serializable;
import java.util.Date;

public class Titulo implements Serializable {

    private int id_titulo;
    private String nombre;
    private String estado;
    private Date fechaVigencia;
    private String urlFoto;
    private boolean maximo;

    public Titulo() {
        this.id_titulo = 0;
    }

    public Titulo(int id_titulo, String nombre, String estado, Date fechaVigencia, String urlFoto, boolean maximo) {
        this.id_titulo = id_titulo;
        this.nombre = nombre;
        this.estado = estado;
        this.fechaVigencia = fechaVigencia;
        this.urlFoto = urlFoto;
        this.maximo = maximo;
    }

    public int getId_titulo() {
        return id_titulo;
    }

    public void setId_titulo(int id_titulo) {
        this.id_titulo = id_titulo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaVigencia() {
        return fechaVigencia;
    }

    public void setFechaVigencia(Date fechaVigencia) {
        this.fechaVigencia = fechaVigencia;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public boolean isMaximo() {
        return maximo;
    }

    public void setMaximo(boolean maximo) {
        this.maximo = maximo;
    }

    @Override
    public String toString() {
        return nombre + (maximo ? " ★" : "");
    }
}
