/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author iagom
 */
public class GrupoPPV {

    private int idGrupoPpv;
    private String nomeGrupo;
    private String descripcionImportancia;
    private String dataHabitual;
    private String fotoUrl;

    public GrupoPPV() {
    }


    public GrupoPPV(int idGrupoPpv, String nomeGrupo, String descripcionImportancia, String dataHabitual, String fotoUrl) {
        this.idGrupoPpv = idGrupoPpv;
        this.nomeGrupo = nomeGrupo;
        this.descripcionImportancia = descripcionImportancia;
        this.dataHabitual = dataHabitual;
        this.fotoUrl = fotoUrl;
    }

    public int getIdGrupoPpv() {
        return idGrupoPpv;
    }

    public void setIdGrupoPpv(int idGrupoPpv) {
        this.idGrupoPpv = idGrupoPpv;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public String getDescripcionImportancia() {
        return descripcionImportancia;
    }

    public void setDescripcionImportancia(String descripcionImportancia) {
        this.descripcionImportancia = descripcionImportancia;
    }

    public String getDataHabitual() {
        return dataHabitual;
    }

    public void setDataHabitual(String dataHabitual) {
        this.dataHabitual = dataHabitual;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    @Override
    public String toString() {
        return nomeGrupo != null ? nomeGrupo : "";
    }
}
