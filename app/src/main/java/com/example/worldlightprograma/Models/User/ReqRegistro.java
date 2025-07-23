package com.example.worldlightprograma.Models.User;

import java.io.Serializable;

public class ReqRegistro implements Serializable {

    private String dni;
    private String nombre;
    private String apellido;
    private String genero;
    private String telefono;
    private String correo;
    private String direccion;
    private String fecha_nacimiento;
    private String username;
    private String password;
    private int membresia_id;
    private String metodo_pago;

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMembresia_id(int membresia_id) {
        this.membresia_id = membresia_id;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public String getDni() {
        return dni;
    }

}
