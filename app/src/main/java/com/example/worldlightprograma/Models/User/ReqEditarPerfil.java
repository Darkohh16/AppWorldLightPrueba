package com.example.worldlightprograma.Models.User;

public class ReqEditarPerfil {
    private String telefono;
    private String correo;
    private String direccion;
    private String username;
    private String password;

    public ReqEditarPerfil(String telefono, String correo, String direccion, String username, String password) {
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.username = username;
        this.password = password;
    }

    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
} 