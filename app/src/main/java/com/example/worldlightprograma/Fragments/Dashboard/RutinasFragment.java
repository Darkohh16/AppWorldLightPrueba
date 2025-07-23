package com.example.worldlightprograma.Fragments.Dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.worldlightprograma.Models.User.RptaRutinaActiva;
import com.example.worldlightprograma.Network.ApiService;
import com.example.worldlightprograma.R;

import com.example.worldlightprograma.Models.User.RptaDetalleRutina;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.example.worldlightprograma.Models.User.ReqEliminarEjercicio;
import com.example.worldlightprograma.Models.User.RptaRegistro;
import android.widget.ProgressBar;

public class RutinasFragment extends Fragment {

    private TextView tvEntrenador, tvFechasRutina, tvObjetivoRutina, tvInicialesEntrenador, tvEntrenadorPersonal;
    private SharedPreferences sharedPreferences;
    private LinearLayout llEjerciciosDia;
    private int rutinaId = -1;
    private Map<String, List<RptaDetalleRutina.EjercicioRutina>> ejerciciosPorDia;
    private String diaSeleccionado = "lunes";
    private final String[] DIAS = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
    private int llamadasPendientes = 0; // Contador para controlar el ProgressBar
    private ProgressBar progressBar;

    private void mostrarProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void ocultarProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void llamadaCompletada() {
        llamadasPendientes--;
        if (llamadasPendientes <= 0) {
            ocultarProgressBar();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rutinas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvEntrenador = view.findViewById(R.id.tvEntrenador);
        tvFechasRutina = view.findViewById(R.id.tvFechasRutina);
        tvObjetivoRutina = view.findViewById(R.id.tvObjetivoRutina);
        tvInicialesEntrenador = view.findViewById(R.id.tvInicialesEntrenador);
        tvEntrenadorPersonal = view.findViewById(R.id.tvEntrenadorPersonal);
        progressBar = view.findViewById(R.id.progressBar);
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", 0);
        llEjerciciosDia = view.findViewById(R.id.llEjerciciosDia);
        setupBotonesDias(view);
        
        // Inicializar contador y mostrar ProgressBar
        llamadasPendientes = 2; // rutina activa + detalle rutina
        mostrarProgressBar();
        cargarRutinaActivaYDetalle();

        view.findViewById(R.id.btnAgregarEjercicio).setOnClickListener(v -> {
            NavHostFragment.findNavController(RutinasFragment.this)
                .navigate(R.id.EjerciciosFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        llamadasPendientes = 2; // rutina activa + detalle rutina
        mostrarProgressBar();
        cargarRutinaActivaYDetalle();
    }

    private void cargarRutinaActivaYDetalle() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        apiService.obtenerRutinaActiva("JWT " + token).enqueue(new Callback<RptaRutinaActiva>() {
            @Override
            public void onResponse(Call<RptaRutinaActiva> call, Response<RptaRutinaActiva> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    RptaRutinaActiva.Data rutina = response.body().getData();
                    rutinaId = rutina.getId();
                    mostrarDatosRutina(rutina);
                    cargarDetalleRutina();
                } else {
                    Toast.makeText(getContext(), "No se pudo obtener la rutina activa", Toast.LENGTH_SHORT).show();
                }
                llamadaCompletada();
            }
            @Override
            public void onFailure(Call<RptaRutinaActiva> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                llamadaCompletada();
            }
        });
    }

    private void cargarDetalleRutina() {
        if (rutinaId == -1) return;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        apiService.detalleRutina("JWT " + token, rutinaId).enqueue(new Callback<RptaDetalleRutina>() {
            @Override
            public void onResponse(Call<RptaDetalleRutina> call, Response<RptaDetalleRutina> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    ejerciciosPorDia = response.body().getData().getEjercicios_por_dia();
                    mostrarEjerciciosDelDia(diaSeleccionado);
                } else {
                    Toast.makeText(getContext(), "No se pudo obtener el detalle de la rutina", Toast.LENGTH_SHORT).show();
                }
                llamadaCompletada();
            }
            @Override
            public void onFailure(Call<RptaDetalleRutina> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                llamadaCompletada();
            }
        });
    }

    private void mostrarDatosRutina(RptaRutinaActiva.Data rutina) {
        String entrenador = rutina.getEntrenador();
        if (entrenador == null || entrenador.trim().isEmpty() || entrenador.equals("null")) {
            tvEntrenador.setText("[Asignación de entrenador pendiente]");
            tvInicialesEntrenador.setText("");
        } else {
            tvEntrenador.setText(entrenador);
            // Iniciales
            String[] partes = entrenador.trim().split(" ");
            String iniciales = "";
            for (String p : partes) {
                if (!p.isEmpty()) iniciales += p.charAt(0);
            }
            tvInicialesEntrenador.setText(iniciales.toUpperCase());
        }
        tvEntrenadorPersonal.setText("(Entrenador Personal)");
        String fechas = "Fechas: " + (rutina.getFecha_inicio() != null ? rutina.getFecha_inicio() : "--");
        if (rutina.getFecha_fin() != null && !rutina.getFecha_fin().isEmpty()) {
            fechas += " al " + rutina.getFecha_fin();
        }
        tvFechasRutina.setText(fechas);
        tvObjetivoRutina.setText("Objetivo: " + (rutina.getObjetivo() != null ? rutina.getObjetivo() : "--"));
    }

    private void setupBotonesDias(View view) {
        final LinearLayout llDias = buscarLinearLayoutDias((ViewGroup) view);
        if (llDias == null) return;
        for (int i = 0; i < llDias.getChildCount(); i++) {
            View v = llDias.getChildAt(i);
            if (v instanceof Button) {
                final int idx = i;
                v.setOnClickListener(btn -> {
                    diaSeleccionado = DIAS[idx];
                    mostrarEjerciciosDelDia(diaSeleccionado);
                    actualizarEstiloBotones(llDias, idx);
                });
            }
        }
        actualizarEstiloBotones(llDias, 0);
    }

    private void actualizarEstiloBotones(LinearLayout llDias, int seleccionado) {
        for (int i = 0; i < llDias.getChildCount(); i++) {
            View v = llDias.getChildAt(i);
            if (v instanceof Button) {
                Button btn = (Button) v;
                if (i == seleccionado) {
                    btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.purple_700));
                    btn.setTextColor(Color.WHITE);
                } else {
                    btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.dia_no_seleccionado));
                    btn.setTextColor(Color.BLACK);
                }
            }
        }
    }

    private void mostrarEjerciciosDelDia(String dia) {
        if (llEjerciciosDia == null) return;
        llEjerciciosDia.removeAllViews();
        if (ejerciciosPorDia == null || !ejerciciosPorDia.containsKey(dia)) {
            TextView tvVacio = new TextView(getContext());
            tvVacio.setText("No hay ejercicios para este día");
            llEjerciciosDia.addView(tvVacio);
            return;
        }
        List<RptaDetalleRutina.EjercicioRutina> ejercicios = ejerciciosPorDia.get(dia);
        for (RptaDetalleRutina.EjercicioRutina ejercicio : ejercicios) {
            CardView card = new CardView(getContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(16, 0, 16, 16);
            card.setLayoutParams(cardParams);
            card.setRadius(12f);
            card.setCardElevation(4f);
            card.setUseCompatPadding(true);

            // Layout horizontal para nombre y botón eliminar
            LinearLayout llHorizontal = new LinearLayout(getContext());
            llHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            llHorizontal.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView tvNombre = new TextView(getContext());
            tvNombre.setText(ejercicio.getNombre());
            tvNombre.setTextSize(16f);
            tvNombre.setTextColor(getResources().getColor(android.R.color.black));
            tvNombre.setTypeface(tvNombre.getTypeface(), android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams nombreParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            tvNombre.setLayoutParams(nombreParams);
            llHorizontal.addView(tvNombre);

            Button btnEliminar = new Button(getContext());
            btnEliminar.setText("Eliminar");
            btnEliminar.setTextColor(Color.WHITE);
            btnEliminar.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_light));
            btnEliminar.setTextSize(13f);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnEliminar.setLayoutParams(btnParams);
            btnEliminar.setOnClickListener(v -> eliminarEjercicioDeRutina(ejercicio, dia));
            llHorizontal.addView(btnEliminar);

            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setPadding(24, 24, 24, 24);
            ll.addView(llHorizontal);

            TextView tvSeries = new TextView(getContext());
            tvSeries.setText(ejercicio.getSeries() + " series x " + ejercicio.getRepeticiones() + " reps");
            tvSeries.setTextSize(14f);
            tvSeries.setTextColor(0xFF666666);
            ll.addView(tvSeries);
            TextView tvDesc = new TextView(getContext());
            tvDesc.setText(ejercicio.getDescripcion());
            tvDesc.setTextSize(13f);
            tvDesc.setTextColor(0xFF666666);
            tvDesc.setPadding(0, 8, 0, 0);
            ll.addView(tvDesc);
            card.addView(ll);
            llEjerciciosDia.addView(card);
        }
    }

    private void eliminarEjercicioDeRutina(RptaDetalleRutina.EjercicioRutina ejercicio, String dia) {
        if (rutinaId == -1) return;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        ReqEliminarEjercicio req = new ReqEliminarEjercicio(rutinaId, ejercicio.getEjercicio_id(), dia);
        apiService.eliminarEjercicioDeRutina("JWT " + token, req).enqueue(new Callback<RptaRegistro>() {
            @Override
            public void onResponse(Call<RptaRegistro> call, Response<RptaRegistro> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    Toast.makeText(getContext(), "Ejercicio eliminado", Toast.LENGTH_SHORT).show();
                    cargarRutinaActivaYDetalle();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "No se pudo eliminar el ejercicio";
                    Toast.makeText(getContext(), "No se pudo eliminar el ejercicio: " + msg, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RptaRegistro> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private LinearLayout buscarLinearLayoutRecursivo(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof LinearLayout && v.getId() != R.id.main) {
                return (LinearLayout) v;
            } else if (v instanceof ViewGroup) {
                LinearLayout res = buscarLinearLayoutRecursivo((ViewGroup) v);
                if (res != null) return res;
            }
        }
        return null;
    }
    private LinearLayout buscarLinearLayoutDias(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof LinearLayout && ((LinearLayout) v).getOrientation() == LinearLayout.HORIZONTAL) {
                // Busca si contiene 7 botones (días)
                int btnCount = 0;
                for (int j = 0; j < ((LinearLayout) v).getChildCount(); j++) {
                    if (((LinearLayout) v).getChildAt(j) instanceof Button) btnCount++;
                }
                if (btnCount == 7) return (LinearLayout) v;
            } else if (v instanceof ViewGroup) {
                LinearLayout res = buscarLinearLayoutDias((ViewGroup) v);
                if (res != null) return res;
            }
        }
        return null;
    }
}