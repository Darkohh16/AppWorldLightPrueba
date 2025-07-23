package com.example.worldlightprograma.Models.User;

import java.util.List;

public class RptaEjercicios {
    private int code;
    private List<Ejercicio> data;
    private String message;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public List<Ejercicio> getData() { return data; }
    public void setData(List<Ejercicio> data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static class Ejercicio {
        private int id;
        private String nombre;
        private String descripcion;
        private String grupo_muscular;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public String getGrupo_muscular() { return grupo_muscular; }
        public void setGrupo_muscular(String grupo_muscular) { this.grupo_muscular = grupo_muscular; }
    }
} 