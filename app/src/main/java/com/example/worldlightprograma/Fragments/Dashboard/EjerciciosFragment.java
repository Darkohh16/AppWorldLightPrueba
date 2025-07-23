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
import android.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.worldlightprograma.Models.User.RptaEjercicios;
import com.example.worldlightprograma.Models.User.RptaEjercicios.Ejercicio;
import com.example.worldlightprograma.Models.User.RptaRutinaActiva;
import com.example.worldlightprograma.Models.User.RptaRegistro;
import com.example.worldlightprograma.Models.User.ReqAgregarEjercicio;
import com.example.worldlightprograma.Network.ApiService;
import com.example.worldlightprograma.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EjerciciosFragment extends Fragment {

    private LinearLayout llGruposEjercicios;
    private SharedPreferences sharedPreferences;
    private Integer rutinaActivaId = null;
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
        return inflater.inflate(R.layout.fragment_ejercicios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llGruposEjercicios = view.findViewById(R.id.llGruposEjercicios);
        progressBar = view.findViewById(R.id.progressBar);
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", 0);
        
        // Inicializar contador y mostrar ProgressBar
        llamadasPendientes = 2; // rutina activa + ejercicios
        mostrarProgressBar();
        cargarRutinaActivaYDespues(this::cargarEjercicios);
    }

    private void cargarEjercicios() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        apiService.obtenerEjercicios("JWT " + token).enqueue(new Callback<RptaEjercicios>() {
            @Override
            public void onResponse(Call<RptaEjercicios> call, Response<RptaEjercicios> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    mostrarEjerciciosAgrupados(response.body().getData());
                } else {
                    Toast.makeText(getContext(), "No se pudieron obtener los ejercicios", Toast.LENGTH_SHORT).show();
                }
                llamadaCompletada();
            }
            @Override
            public void onFailure(Call<RptaEjercicios> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                llamadaCompletada();
            }
        });
    }

    private void cargarRutinaActivaYDespues(Runnable despues) {
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
                    rutinaActivaId = response.body().getData().getId();
                } else {
                    rutinaActivaId = null;
                }
                despues.run();
                llamadaCompletada();
            }
            @Override
            public void onFailure(Call<RptaRutinaActiva> call, Throwable t) {
                rutinaActivaId = null;
                despues.run();
                llamadaCompletada();
            }
        });
    }

    private void mostrarEjerciciosAgrupados(List<Ejercicio> ejercicios) {
        llGruposEjercicios.removeAllViews();
        Map<String, List<Ejercicio>> grupos = new HashMap<>();
        for (Ejercicio e : ejercicios) {
            String grupo = e.getGrupo_muscular() != null ? e.getGrupo_muscular() : "Otros";
            if (!grupos.containsKey(grupo)) grupos.put(grupo, new ArrayList<>());
            grupos.get(grupo).add(e);
        }
        for (String grupo : grupos.keySet()) {
            // Header del grupo muscular
            TextView tvGrupo = new TextView(getContext());
            tvGrupo.setText(grupo);
            tvGrupo.setTextSize(18f);
            tvGrupo.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_blue));
            tvGrupo.setTypeface(tvGrupo.getTypeface(), android.graphics.Typeface.BOLD);
            tvGrupo.setPadding(16, 24, 16, 8);
            llGruposEjercicios.addView(tvGrupo);
            // Cards de ejercicios
            for (Ejercicio e : grupos.get(grupo)) {
                CardView card = new CardView(getContext());
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(16, 0, 16, 12);
                card.setLayoutParams(cardParams);
                card.setRadius(12f);
                card.setCardElevation(4f);
                card.setUseCompatPadding(true);
                LinearLayout ll = new LinearLayout(getContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setPadding(16, 16, 16, 16);
                // Nombre
                TextView tvNombre = new TextView(getContext());
                tvNombre.setText(e.getNombre());
                tvNombre.setTextSize(16f);
                tvNombre.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                tvNombre.setTypeface(tvNombre.getTypeface(), android.graphics.Typeface.BOLD);
                ll.addView(tvNombre);
                // Descripción
                TextView tvDesc = new TextView(getContext());
                tvDesc.setText(e.getDescripcion());
                tvDesc.setTextSize(14f);
                tvDesc.setTextColor(0xFF666666);
                tvDesc.setPadding(0, 4, 0, 0);
                ll.addView(tvDesc);
                // Botón agregar (opcional, aquí solo de ejemplo)
                Button btnAgregar = new Button(getContext());
                btnAgregar.setText("Agregar");
                btnAgregar.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
                btnAgregar.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.primary_blue_light));
                btnAgregar.setTextSize(14f);
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                btnParams.topMargin = 8;
                btnParams.gravity = android.view.Gravity.END;
                btnAgregar.setLayoutParams(btnParams);
                // Aquí puedes setear el clickListener para agregar el ejercicio a la rutina
                btnAgregar.setOnClickListener(v -> {
                    if (rutinaActivaId == null) {
                        Toast.makeText(getContext(), "No hay rutina activa para agregar ejercicios", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mostrarDialogoAgregarEjercicio(e);
                });
                ll.addView(btnAgregar);
                card.addView(ll);
                llGruposEjercicios.addView(card);
            }
        }
    }

    private void mostrarDialogoAgregarEjercicio(RptaEjercicios.Ejercicio ejercicio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Agregar ejercicio a rutina");
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_ejercicio, null, false);
        builder.setView(dialogView);
        // Día de la semana
        String[] dias = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, dias);
        android.widget.Spinner spinnerDia = dialogView.findViewById(R.id.spinnerDia);
        spinnerDia.setAdapter(adapterDias);
        // Series y repeticiones
        EditText etSeries = dialogView.findViewById(R.id.etSeries);
        EditText etReps = dialogView.findViewById(R.id.etReps);
        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String dia = spinnerDia.getSelectedItem().toString();
            String series = etSeries.getText().toString().trim();
            String reps = etReps.getText().toString().trim();
            if (series.isEmpty() || reps.isEmpty()) {
                Toast.makeText(getContext(), "Completa series y repeticiones", Toast.LENGTH_SHORT).show();
                return;
            }
            agregarEjercicioARutina(ejercicio.getId(), dia, series, reps);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void agregarEjercicioARutina(int ejercicioId, String dia, String series, String reps) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        // Usar modelo dedicado
        ReqAgregarEjercicio req = new ReqAgregarEjercicio(
            rutinaActivaId,
            ejercicioId,
            Integer.parseInt(series),
            Integer.parseInt(reps),
            dia
        );
        retrofit2.Call<RptaRegistro> call = apiService.agregarEjercicioARutina("JWT " + token, req);
        call.enqueue(new Callback<RptaRegistro>() {
            @Override
            public void onResponse(Call<RptaRegistro> call, Response<RptaRegistro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RptaRegistro rpta = response.body();
                    if (rpta.getCode() == 1) {
                        Toast.makeText(getContext(), "Ejercicio agregado a la rutina", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(EjerciciosFragment.this)
                            .navigateUp();
                    } else {
                        Toast.makeText(getContext(), "Error: " + rpta.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<RptaRegistro> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}