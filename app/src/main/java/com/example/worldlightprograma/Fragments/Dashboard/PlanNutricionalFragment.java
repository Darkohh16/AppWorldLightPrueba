package com.example.worldlightprograma.Fragments.Dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldlightprograma.Models.User.RptaClasesInscritas;
import com.example.worldlightprograma.Models.User.RptaPlanNutricional;
import com.example.worldlightprograma.Network.ApiService;
import com.example.worldlightprograma.R;
import com.example.worldlightprograma.databinding.FragmentPlanNutricionalBinding;
import com.example.worldlightprograma.databinding.ItemComidaBinding;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.util.Log;
import com.google.gson.Gson;
import android.graphics.Color;
import androidx.core.content.ContextCompat;

public class PlanNutricionalFragment extends Fragment {

    private FragmentPlanNutricionalBinding binding;
    private String token;
    SharedPreferences sharedPreferences;
    private RptaPlanNutricional.Data planData;
    private String diaSeleccionado = "lunes";
    private String nombreUsuario = "";
    private String apellidoUsuario = "";
    private String nombreNutricionista = "";
    private int llamadasPendientes = 0; // Contador para controlar el ProgressBar

    private void mostrarProgressBar() {
        if (binding != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void ocultarProgressBar() {
        if (binding != null) {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void llamadaCompletada() {
        llamadasPendientes--;
        if (llamadasPendientes <= 0) {
            ocultarProgressBar();
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentPlanNutricionalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            nombreUsuario = getArguments().getString("nombre", "");
            apellidoUsuario = getArguments().getString("apellido", "");
        }
        binding.tvNombre.setText(nombreUsuario + " " + apellidoUsuario);
        binding.tvIniciales.setText(obtenerIniciales(nombreUsuario, apellidoUsuario));
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Inicializar contador y mostrar ProgressBar
        llamadasPendientes = 1; // solo plan nutricional
        mostrarProgressBar();
        obtenerPlanNutricional();
        setupBotonesDias();
    }

    private void obtenerPlanNutricional() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService wLPrograma = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        Call<RptaPlanNutricional> call = wLPrograma.obtenerPlanNutricional("JWT " + token);

        call.enqueue(new Callback<RptaPlanNutricional>() {
            @Override
            public void onResponse(Call<RptaPlanNutricional> call, Response<RptaPlanNutricional> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    planData = response.body().getData();
                    mostrarDatosPlan();
                    mostrarComidasDelDia(diaSeleccionado);
                } else {
                    Toast.makeText(getContext(), "Aun no tienes un plan nutricional asociado", Toast.LENGTH_SHORT).show();
                }
                llamadaCompletada();
            }
            @Override
            public void onFailure(Call<RptaPlanNutricional> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                llamadaCompletada();
            }
        });
    }

    private void mostrarDatosPlan() {
        binding.tvObjetivo.setText("Objetivo: " + planData.getObjetivo());
        if (planData.getNutricionista() != null && !planData.getNutricionista().isEmpty()) {
            binding.tvNutricionista.setText("Nutricionista: " + planData.getNutricionista());
        } else {
            binding.tvNutricionista.setText("");
        }
        binding.tvFechas.setText("Inicio: " + planData.getFecha_inicio() + "  Fin: " + planData.getFecha_fin());
    }

    private void mostrarComidasDelDia(String dia) {
        if (planData == null || planData.getPlan_por_dia() == null) {
            Toast.makeText(getContext(), "No hay plan por día disponible", Toast.LENGTH_SHORT).show();
            binding.llComidasDia.removeAllViews();
            binding.tvCalorias.setText("0 kcal");
            return;
        }
        // Buscar la clave ignorando tildes y mayúsculas
        String claveReal = null;
        for (String key : planData.getPlan_por_dia().keySet()) {
            if (normaliza(key).equals(normaliza(dia))) {
                claveReal = key;
                break;
            }
        }
        if (claveReal == null) {
            Toast.makeText(getContext(), "No hay comidas para este día", Toast.LENGTH_SHORT).show();
            binding.llComidasDia.removeAllViews();
            binding.tvCalorias.setText("0 kcal");
            return;
        }
        List<RptaPlanNutricional.Comida> comidas = planData.getPlan_por_dia().get(claveReal);
        binding.llComidasDia.removeAllViews();
        double totalCalorias = 0;
        if (comidas != null && !comidas.isEmpty()) {
            for (RptaPlanNutricional.Comida comida : comidas) {
                ItemComidaBinding itemBinding = ItemComidaBinding.inflate(getLayoutInflater(), binding.llComidasDia, false);
                itemBinding.tvMomento.setText(capitalize(comida.getMomento()));
                itemBinding.tvComida.setText(comida.getComida());
                itemBinding.tvDescripcion.setText(comida.getDescripcion());
                itemBinding.tvCalorias.setText((comida.getCalorias() != null ? comida.getCalorias().intValue() : 0) + " kcal");
                // Ocultar el campo de nutricionista si existe en el layout
                itemBinding.tvNutricionista.setVisibility(View.GONE);
                binding.llComidasDia.addView(itemBinding.getRoot());
                totalCalorias += comida.getCalorias() != null ? comida.getCalorias() : 0;
            }
            binding.tvCalorias.setText(((int)totalCalorias) + " kcal");
        } else {
            TextView tvVacio = new TextView(getContext());
            tvVacio.setText("Aún no hay comidas registradas para este día");
            binding.llComidasDia.addView(tvVacio);
            binding.tvCalorias.setText("0 kcal");
        }
    }

    private void setupBotonesDias() {
        final String[] dias = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
        for (int i = 0; i < binding.llDias.getChildCount(); i++) {
            View v = binding.llDias.getChildAt(i);
            if (v instanceof Button) {
                final int idx = i;
                v.setOnClickListener(view -> {
                    diaSeleccionado = dias[idx];
                    mostrarComidasDelDia(diaSeleccionado);
                    actualizarEstiloBotones(idx);
                });
            }
        }
        // Inicializa el estilo al cargar
        actualizarEstiloBotones(0);
    }

    private void actualizarEstiloBotones(int seleccionado) {
        for (int i = 0; i < binding.llDias.getChildCount(); i++) {
            View v = binding.llDias.getChildAt(i);
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

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    private String normaliza(String input) {
        return java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase().trim();
    }

    private String obtenerIniciales(String nombre, String apellido) {
        String ini = "";
        if (nombre != null && !nombre.isEmpty()) ini += nombre.charAt(0);
        if (apellido != null && !apellido.isEmpty()) ini += apellido.charAt(0);
        return ini.toUpperCase();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}