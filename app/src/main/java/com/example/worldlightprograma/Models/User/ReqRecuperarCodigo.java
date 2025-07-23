package com.example.worldlightprograma.Models.User;

public class ReqRecuperarCodigo {
    private String correo;
    private String codigo;
    public ReqRecuperarCodigo(String correo, String codigo) {
        this.correo = correo;
        this.codigo = codigo;
    }
    public String getCorreo() { return correo; }
    public String getCodigo() { return codigo; }
} 