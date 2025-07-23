package com.example.worldlightprograma.Models.User;

import androidx.savedstate.SavedStateReader;

public class RptaDatosPrincipal {
    private int code;
    private String message;
    private datos data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public datos getData() {
        return data;
    }

    public static class datos {
        private String nombre;
        private String apellido;
        private String membresia;
        private String fecha_vencimiento;
        private String estado;

        public String getNombre() {
            return nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public String getMembresia() {
            return membresia;
        }

        public String getFecha_vencimiento() {
            return fecha_vencimiento;
        }

        public String getEstado() {
            return estado;
        }
    }


}
