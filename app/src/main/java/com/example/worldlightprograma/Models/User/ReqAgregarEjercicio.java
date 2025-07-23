package com.example.worldlightprograma.Models.User;

public class ReqAgregarEjercicio {
    private int rutina_id;
    private int ejercicio_id;
    private int series;
    private int repeticiones;
    private String dia_semana;

    public ReqAgregarEjercicio(int rutina_id, int ejercicio_id, int series, int repeticiones, String dia_semana) {
        this.rutina_id = rutina_id;
        this.ejercicio_id = ejercicio_id;
        this.series = series;
        this.repeticiones = repeticiones;
        this.dia_semana = dia_semana;
    }

    public int getRutina_id() { return rutina_id; }
    public int getEjercicio_id() { return ejercicio_id; }
    public int getSeries() { return series; }
    public int getRepeticiones() { return repeticiones; }
    public String getDia_semana() { return dia_semana; }
} 