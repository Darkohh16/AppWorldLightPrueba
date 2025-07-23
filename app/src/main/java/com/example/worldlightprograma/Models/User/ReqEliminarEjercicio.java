package com.example.worldlightprograma.Models.User;

public class ReqEliminarEjercicio {
    private int rutina_id;
    private int ejercicio_id;
    private String dia_semana;

    public ReqEliminarEjercicio(int rutina_id, int ejercicio_id, String dia_semana) {
        this.rutina_id = rutina_id;
        this.ejercicio_id = ejercicio_id;
        this.dia_semana = dia_semana;
    }

    public int getRutina_id() { return rutina_id; }
    public int getEjercicio_id() { return ejercicio_id; }
    public String getDia_semana() { return dia_semana; }
} 