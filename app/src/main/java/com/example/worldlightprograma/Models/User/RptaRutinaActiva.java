package com.example.worldlightprograma.Models.User;

public class RptaRutinaActiva {
    private int code;
    private Data data;
    private String message;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public static class Data {
        private int id;
        private String fecha_inicio;
        private String fecha_fin;
        private String objetivo;
        private String estado;
        private String entrenador;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getFecha_inicio() { return fecha_inicio; }
        public void setFecha_inicio(String fecha_inicio) { this.fecha_inicio = fecha_inicio; }

        public String getFecha_fin() { return fecha_fin; }
        public void setFecha_fin(String fecha_fin) { this.fecha_fin = fecha_fin; }

        public String getObjetivo() { return objetivo; }
        public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        public String getEntrenador() { return entrenador; }
        public void setEntrenador(String entrenador) { this.entrenador = entrenador; }
    }
} 