package model;

public class Combate {

    private int idCombate;
    private int idPpv;
    private Integer idTituloEnXogo;
    private Integer idLoitadorGanador; 
    private String tipoCombate;
    private int ordeNoPpv;

    public Combate() {
    }

    public int getIdCombate() {
        return idCombate;
    }

    public void setIdCombate(int idCombate) {
        this.idCombate = idCombate;
    }

    public int getIdPpv() {
        return idPpv;
    }

    public void setIdPpv(int idPpv) {
        this.idPpv = idPpv;
    }

    public Integer getIdTituloEnXogo() {
        return idTituloEnXogo;
    }

    public void setIdTituloEnXogo(Integer idTituloEnXogo) {
        this.idTituloEnXogo = idTituloEnXogo;
    }

    public Integer getIdLoitadorGanador() {
        return idLoitadorGanador;
    }

    public void setIdLoitadorGanador(Integer idLoitadorGanador) {
        this.idLoitadorGanador = idLoitadorGanador;
    }

    public String getTipoCombate() {
        return tipoCombate;
    }

    public void setTipoCombate(String tipoCombate) {
        this.tipoCombate = tipoCombate;
    }

    public int getOrdeNoPpv() {
        return ordeNoPpv;
    }

    public void setOrdeNoPpv(int ordeNoPpv) {
        this.ordeNoPpv = ordeNoPpv;
    }
}
