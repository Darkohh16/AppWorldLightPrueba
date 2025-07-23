package com.example.worldlightprograma.Fragments.Dashboard;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldlightprograma.Models.User.ReqCrearRutina;
import com.example.worldlightprograma.Models.User.RptaRegistro;
import com.example.worldlightprograma.Network.ApiService;
import com.example.worldlightprograma.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrearRutinaFragment extends Fragment {

    private EditText etObjetivo, etFechaInicio, etFechaFin;
    private Button btnCrearRutina;
    private TextView tvMensaje;
    private SharedPreferences sharedPreferences;
    private Calendar calendarInicio, calendarFin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crear_rutina, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etObjetivo = view.findViewById(R.id.etObjetivo);
        etFechaInicio = view.findViewById(R.id.etFechaInicio);
        etFechaFin = view.findViewById(R.id.etFechaFin);
        btnCrearRutina = view.findViewById(R.id.btnCrearRutina);
        tvMensaje = view.findViewById(R.id.tvMensaje);
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", 0);
        calendarInicio = Calendar.getInstance();
        calendarFin = Calendar.getInstance();

        etFechaInicio.setOnClickListener(v -> mostrarDatePicker(etFechaInicio, calendarInicio));
        etFechaFin.setOnClickListener(v -> mostrarDatePicker(etFechaFin, calendarFin));

        btnCrearRutina.setOnClickListener(v -> crearRutina());
    }

    private void mostrarDatePicker(EditText editText, Calendar calendar) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            editText.setText(sdf.format(calendar.getTime()));
        };
        new DatePickerDialog(requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void crearRutina() {
        String objetivo = etObjetivo.getText().toString().trim();
        String fechaInicio = etFechaInicio.getText().toString().trim();
        String fechaFin = etFechaFin.getText().toString().trim();

        if (objetivo.isEmpty() || fechaInicio.isEmpty()) {
            Toast.makeText(getContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            tvMensaje.setText("Completa los campos obligatorios");
            tvMensaje.setVisibility(View.VISIBLE);
            return;
        }
        tvMensaje.setVisibility(View.GONE);

        ReqCrearRutina req = new ReqCrearRutina();
        req.setObjetivo(objetivo);
        req.setFecha_inicio(fechaInicio);
        req.setFecha_fin(fechaFin.isEmpty() ? null : fechaFin);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService wLPrograma = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        wLPrograma.crearRutina("JWT " + token, req).enqueue(new Callback<RptaRegistro>() {
            @Override
            public void onResponse(Call<RptaRegistro> call, Response<RptaRegistro> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    Toast.makeText(getContext(), "Rutina creada", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(CrearRutinaFragment.this)
                            .navigate(CrearRutinaFragmentDirections.actionCrearRutinaFragmentToPrincipalFragment());
                } else {
                    tvMensaje.setText("Error al crear rutina");
                    tvMensaje.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<RptaRegistro> call, Throwable t) {
                tvMensaje.setText("Error de conexión");
                tvMensaje.setVisibility(View.VISIBLE);
            }
        });
    }
}