package com.example.worldlightprograma.Models.User;

public class ReqRecuperarNuevaContrasena {
    private String correo;
    private String codigo;
    private String nueva_contrasena;
    public ReqRecuperarNuevaContrasena(String correo, String codigo, String nueva_contrasena) {
        this.correo = correo;
        this.codigo = codigo;
        this.nueva_contrasena = nueva_contrasena;
    }
    public String getCorreo() { return correo; }
    public String getCodigo() { return codigo; }
    public String getNueva_contrasena() { return nueva_contrasena; }
} 