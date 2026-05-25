package model;

public class Marca {

    private int id_marca;
    private String nome_marca;
    private String logo_url;

    public Marca() {
    }

    public Marca(int id_marca, String nome_marca, String logo_url) {
        this.id_marca = id_marca;
        this.nome_marca = nome_marca;
        this.logo_url = logo_url;
    }

    public int getId_marca() {
        return id_marca;
    }

    public void setId_marca(int id_marca) {
        this.id_marca = id_marca;
    }

    public String getNome_marca() {
        return nome_marca;
    }

    public void setNome_marca(String nome_marca) {
        this.nome_marca = nome_marca;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    @Override
    public String toString() {
        return nome_marca;
    }
}
