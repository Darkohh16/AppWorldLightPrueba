package com.example.worldlightprograma.Models.User;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RptaClasesDisponibles {
    private int code;
    private String message;
    private List<ClaseDisponible> data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public List<ClaseDisponible> getData() { return data; }

    public static class ClaseDisponible {
        private int capacidad;
        private String descripcion;
        @SerializedName("dia_semana")
        private String diaSemana;
        @SerializedName("hora_fin")
        private String horaFin;
        @SerializedName("hora_inicio")
        private String horaInicio;
        private int id;
        private String instructor;
        @SerializedName("nombre")
        private String nombreClase;

        public int getCapacidad() { return capacidad; }
        public String getDescripcion() { return descripcion; }
        public String getDiaSemana() { return diaSemana; }
        public String getHoraFin() { return horaFin; }
        public String getHoraInicio() { return horaInicio; }
        public int getId() { return id; }
        public String getInstructor() { return instructor; }
        public String getNombreClase() { return nombreClase; }
    }
} 