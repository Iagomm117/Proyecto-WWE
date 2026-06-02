package model;


public class Loitador {

    private int id_loitador;
    private String nome;
    private String estado;
    private String categoria_peso;
    private String entrada;
    private String foto_url;
    private int veces_consultado;

    public Loitador() {
    }

    public Loitador(int id_loitador, String nome, String estado, String categoria_peso, String entrada, String foto_url, int veces_consultado) {
        this.id_loitador = id_loitador;
        this.nome = nome;
        this.estado = estado;
        this.categoria_peso = categoria_peso;
        this.entrada = entrada;
        this.foto_url = foto_url;
        this.veces_consultado = veces_consultado;
    }


    public int getId_loitador() {
        return id_loitador;
    }

    public void setId_loitador(int id_loitador) {
        this.id_loitador = id_loitador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria_peso() {
        return categoria_peso;
    }

    public void setCategoria_peso(String categoria_peso) {
        this.categoria_peso = categoria_peso;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getFoto_url() {
        return foto_url;
    }

    public void setFoto_url(String foto_url) {
        this.foto_url = foto_url;
    }

    public int getVeces_consultado() {
        return veces_consultado;
    }

    public void setVeces_consultado(int veces_consultado) {
        this.veces_consultado = veces_consultado;
    }
    
    
    @Override
    public String toString() {
        return nome;
    }
}
