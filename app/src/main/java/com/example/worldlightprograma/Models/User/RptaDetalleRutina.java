package com.example.worldlightprograma.Models.User;

import java.util.List;
import java.util.Map;

public class RptaDetalleRutina {
    private int code;
    private String message;
    private Data data;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public static class Data {
        private int id;
        private String objetivo;
        private String estado;
        private String entrenador;
        private String fecha_inicio;
        private String fecha_fin;
        private Map<String, List<EjercicioRutina>> ejercicios_por_dia;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getObjetivo() { return objetivo; }
        public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public String getEntrenador() { return entrenador; }
        public void setEntrenador(String entrenador) { this.entrenador = entrenador; }
        public String getFecha_inicio() { return fecha_inicio; }
        public void setFecha_inicio(String fecha_inicio) { this.fecha_inicio = fecha_inicio; }
        public String getFecha_fin() { return fecha_fin; }
        public void setFecha_fin(String fecha_fin) { this.fecha_fin = fecha_fin; }
        public Map<String, List<EjercicioRutina>> getEjercicios_por_dia() { return ejercicios_por_dia; }
        public void setEjercicios_por_dia(Map<String, List<EjercicioRutina>> ejercicios_por_dia) { this.ejercicios_por_dia = ejercicios_por_dia; }
    }

    public static class EjercicioRutina {
        private int ejercicio_id;
        private String nombre;
        private String descripcion;
        private String grupo_muscular;
        private int series;
        private String repeticiones;

        public int getEjercicio_id() { return ejercicio_id; }
        public void setEjercicio_id(int ejercicio_id) { this.ejercicio_id = ejercicio_id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public String getGrupo_muscular() { return grupo_muscular; }
        public void setGrupo_muscular(String grupo_muscular) { this.grupo_muscular = grupo_muscular; }
        public int getSeries() { return series; }
        public void setSeries(int series) { this.series = series; }
        public String getRepeticiones() { return repeticiones; }
        public void setRepeticiones(String repeticiones) { this.repeticiones = repeticiones; }
    }
} 