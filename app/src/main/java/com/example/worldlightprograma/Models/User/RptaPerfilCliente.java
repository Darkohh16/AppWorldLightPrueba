package com.example.worldlightprograma.Models.User;

public class RptaPerfilCliente {
    private int code;
    private String message;
    private Data data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        private String nombre;
        private String apellido;
        private String correo;
        private String dni;
        private String telefono;
        private String direccion;
        private String username;
        private String fecha_registro;
        private String estado;

        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getCorreo() { return correo; }
        public String getDni() { return dni; }
        public String getTelefono() { return telefono; }
        public String getDireccion() { return direccion; }
        public String getUsername() { return username; }
        public String getFecha_registro() { return fecha_registro; }
        public String getEstado() { return estado; }
    }
} 