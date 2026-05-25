package model;

import java.io.Serializable;
import java.util.Date;

public class PPV implements Serializable {

    private int id_ppv;
    private String nombre;
    private Date dataCelebracion;
    private String estado;
    private String localizacion;
    private String urlPoster;
    private GrupoPPV grupoPPV;

    public PPV() {
        this.id_ppv = 0;
        this.estado = "pendente";
    }

    public PPV(int id_ppv, String nombre, Date dataCelebracion, String estado, String localizacion, String urlPoster, GrupoPPV grupoPPV) {
        this.id_ppv = id_ppv;
        this.nombre = nombre;
        this.dataCelebracion = dataCelebracion;
        this.estado = estado;
        this.localizacion = localizacion;
        this.urlPoster = urlPoster;
        this.grupoPPV = grupoPPV;
    }

    public int getId_ppv() {
        return id_ppv;
    }

    public void setId_ppv(int id_ppv) {
        this.id_ppv = id_ppv;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getDataCelebracion() {
        return dataCelebracion;
    }

    public void setDataCelebracion(Date dataCelebracion) {
        this.dataCelebracion = dataCelebracion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public String getUrlPoster() {
        return urlPoster;
    }

    public void setUrlPoster(String urlPoster) {
        this.urlPoster = urlPoster;
    }

    public GrupoPPV getGrupoPPV() {
        return grupoPPV;
    }

    public void setGrupoPPV(GrupoPPV grupoPPV) {
        this.grupoPPV = grupoPPV;
    }

    @Override
    public String toString() {
        return nombre;
    }
}