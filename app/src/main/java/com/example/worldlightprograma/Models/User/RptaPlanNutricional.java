package com.example.worldlightprograma.Models.User;

import java.util.Map;
import java.util.List;

public class RptaPlanNutricional {
    private int code;
    private Data data;
    private String message;

    public int getCode() { return code; }
    public Data getData() { return data; }
    public String getMessage() { return message; }

    public static class Data {
        private String fecha_inicio;
        private String fecha_fin;
        private String objetivo;
        private String estado;
        private String nutricionista;
        private Map<String, List<Comida>> plan_por_dia;

        public String getFecha_inicio() { return fecha_inicio; }
        public String getFecha_fin() { return fecha_fin; }
        public String getObjetivo() { return objetivo; }
        public String getEstado() { return estado; }
        public String getNutricionista() { return nutricionista; }
        public Map<String, List<Comida>> getPlan_por_dia() { return plan_por_dia; }
    }

    public static class Comida {
        private String momento;
        private String comida;
        private String descripcion;
        private Double calorias;

        public String getMomento() { return momento; }
        public String getComida() { return comida; }
        public String getDescripcion() { return descripcion; }
        public Double getCalorias() { return calorias; }
    }
}
