package model;

import java.io.Serializable;

public class Equipo implements Serializable {

    private int id_equipo;
    private String nome_equipo;
    private Marca marca;
    private String descripcion;
    private String foto_url;

    public Equipo() {
        this.id_equipo = 0;
    }

    public int getId_equipo() {
        return id_equipo;
    }

    public void setId_equipo(int id_equipo) {
        this.id_equipo = id_equipo;
    }

    public String getNome_equipo() {
        return nome_equipo;
    }

    public void setNome_equipo(String nome_equipo) {
        this.nome_equipo = nome_equipo;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto_url() {
        return foto_url;
    }

    public void setFoto_url(String foto_url) {
        this.foto_url = foto_url;
    }

    @Override
    public String toString() {
        return nome_equipo;
    }
}
