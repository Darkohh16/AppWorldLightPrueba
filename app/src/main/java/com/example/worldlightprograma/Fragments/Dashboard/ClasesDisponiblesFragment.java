package com.example.worldlightprograma.Fragments.Dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.LinearLayout;

import com.example.worldlightprograma.Models.User.RptaPlanNutricional;
import com.example.worldlightprograma.Network.ApiService;
import com.example.worldlightprograma.R;
import com.example.worldlightprograma.databinding.FragmentClasesDisponiblesBinding;
import com.example.worldlightprograma.databinding.FragmentPlanNutricionalBinding;
import com.example.worldlightprograma.databinding.ItemComidaBinding;
import com.example.worldlightprograma.Models.User.RptaClasesInscritas;
import com.example.worldlightprograma.Models.User.RptaClasesDisponibles;
import com.example.worldlightprograma.Models.User.ReqInscribirseClase;
import com.example.worldlightprograma.Models.User.RptaRegistro;

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
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ClasesDisponiblesFragment extends Fragment {

    private FragmentClasesDisponiblesBinding binding;
    SharedPreferences sharedPreferences;
    private LinearLayout llClasesContainer;
    private Button btnInscribirse;
    private List<RptaClasesDisponibles.ClaseDisponible> clasesDisponibles = new ArrayList<>();
    private RptaClasesDisponibles.ClaseDisponible claseSeleccionada = null;
    private List<RadioButton> radioButtons = new ArrayList<>();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_clases_disponibles, container, false);
        llClasesContainer = view.findViewById(R.id.llClasesContainer);
        btnInscribirse = view.findViewById(R.id.btnInscribirse);

        btnInscribirse.setOnClickListener(v -> {
            if (claseSeleccionada == null) {
                Toast.makeText(getContext(), "Debes escoger una clase", Toast.LENGTH_SHORT).show();
            } else {
                sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", 0);
                String token = sharedPreferences.getString("token", "");
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService apiService = retrofit.create(ApiService.class);
                ReqInscribirseClase req = new ReqInscribirseClase(claseSeleccionada.getId());
                Call<RptaRegistro> call = apiService.inscribirseClase("JWT " + token, req);
                call.enqueue(new retrofit2.Callback<RptaRegistro>() {
                    @Override
                    public void onResponse(Call<RptaRegistro> call, Response<RptaRegistro> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                            Toast.makeText(getContext(), "¡Te has inscrito en la clase!", Toast.LENGTH_LONG).show();
                            NavHostFragment.findNavController(ClasesDisponiblesFragment.this).popBackStack();
                        } else {
                            String msg = response.body() != null ? response.body().getMessage() : "No se pudo inscribir";
                            Toast.makeText(getContext(), "No se pudo inscribir: " + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RptaRegistro> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de red al inscribirse", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        cargarClasesDisponibles();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);

    }
    

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void cargarClasesDisponibles() {
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", 0);
        String token = sharedPreferences.getString("token", "");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<RptaClasesDisponibles> call = apiService.obtenerClasesDisponibles("JWT " + token);
        call.enqueue(new Callback<RptaClasesDisponibles>() {
            @Override
            public void onResponse(Call<RptaClasesDisponibles> call, Response<RptaClasesDisponibles> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                RptaClasesDisponibles rpta = response.body();
                if (rpta.getCode() == 1) {
                    clasesDisponibles.clear();
                    clasesDisponibles.addAll(rpta.getData());
                    mostrarClasesEnCards();
                } else {
                    Toast.makeText(getContext(), "No hay clases disponibles", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RptaClasesDisponibles> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar clases", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarClasesEnCards() {
        llClasesContainer.removeAllViews();
        radioButtons.clear();
        claseSeleccionada = null;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (RptaClasesDisponibles.ClaseDisponible clase : clasesDisponibles) {
            View cardView = inflater.inflate(R.layout.item_clase_disponible, llClasesContainer, false);
            TextView tvHora = cardView.findViewById(R.id.tvHora);
            TextView tvClase = cardView.findViewById(R.id.tvClase);
            TextView tvProfesor = cardView.findViewById(R.id.tvInstructor);
            TextView tvDuracion = cardView.findViewById(R.id.tvDuracion);
            RadioButton radio = cardView.findViewById(R.id.radioSeleccionarClase);

            String horaInicio = clase.getHoraInicio() != null ? clase.getHoraInicio().substring(0, 5) : "";
            String horaFin = clase.getHoraFin() != null ? clase.getHoraFin().substring(0, 5) : "";
            String nombreClase = clase.getNombreClase() != null ? clase.getNombreClase() : "(Sin nombre)";

            tvHora.setText(horaInicio);
            tvClase.setText(nombreClase);
            tvProfesor.setText(clase.getInstructor());
            tvDuracion.setText(horaFin);

            radio.setChecked(false);
            radio.setOnClickListener(v -> {
                for (RadioButton rb : radioButtons) rb.setChecked(false);
                radio.setChecked(true);
                claseSeleccionada = clase;
            });
            radioButtons.add(radio);
            llClasesContainer.addView(cardView);
        }
    }
}