package com.example.worldlightprograma.Models.User;

import java.util.List;

public class RptaClasesInscritas {
    private int code;
    private String message;
    private List<datos> data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<datos> getData() {
        return data;
    }

    public static class datos {
        private int clase_id;
        private String clase;
        private String descripcion;
        private int capacidad;
        private String instructor;
        private String dia_semana;
        private String hora_inicio;
        private String hora_fin;

        public int getClase_id() {
            return clase_id;
        }

        public String getClase() {
            return clase;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public int getCapacidad() {
            return capacidad;
        }

        public String getInstructor() {
            return instructor;
        }

        public String getDia_semana() {
            return dia_semana;
        }

        public String getHora_inicio() {
            return hora_inicio;
        }

        public String getHora_fin() {
            return hora_fin;
        }
    }


}
