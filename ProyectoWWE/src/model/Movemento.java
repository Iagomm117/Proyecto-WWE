package model;

public class Movemento {

    private int idMovemento;
    private String nomeMovemento;
    private String descripcion;

    public Movemento() {
    }

    public Movemento(int idMovemento, String nomeMovemento, String descripcion) {
        this.idMovemento = idMovemento;
        this.nomeMovemento = nomeMovemento;
        this.descripcion = descripcion;
    }

    public int getIdMovemento() {
        return idMovemento;
    }

    public void setIdMovemento(int idMovemento) {
        this.idMovemento = idMovemento;
    }

    public String getNomeMovemento() {
        return nomeMovemento;
    }

    public void setNomeMovemento(String nomeMovemento) {
        this.nomeMovemento = nomeMovemento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nomeMovemento;
    }
}
