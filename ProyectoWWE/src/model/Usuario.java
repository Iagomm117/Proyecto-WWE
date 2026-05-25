package model;

import java.time.LocalDateTime;

public class Usuario {
    private int idUsuario;
    private String nomeUsuario;
    private String email;
    private String contraseinalHash;
    private String rol;
    private String fotoUrl;
    private LocalDateTime dataRexistro;
    
    public Usuario() {}
    
    public Usuario(int idUsuario, String nomeUsuario, String email, String contraseinalHash, String rol) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.contraseinalHash = contraseinalHash;
        this.rol = rol;
    }
    
    public Usuario(int idUsuario, String nomeUsuario, String email, String contraseinalHash, 
                   String rol, String fotoUrl, LocalDateTime dataRexistro) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.contraseinalHash = contraseinalHash;
        this.rol = rol;
        this.fotoUrl = fotoUrl;
        this.dataRexistro = dataRexistro;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getNomeUsuario() {
        return nomeUsuario;
    }
    
    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getContraseinalHash() {
        return contraseinalHash;
    }
    
    public void setContraseinalHash(String contraseinalHash) {
        this.contraseinalHash = contraseinalHash;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public String getFotoUrl() {
        return fotoUrl;
    }
    
    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
    
    public LocalDateTime getDataRexistro() {
        return dataRexistro;
    }
    
    public void setDataRexistro(LocalDateTime dataRexistro) {
        this.dataRexistro = dataRexistro;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + idUsuario +
                ", nome='" + nomeUsuario + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}