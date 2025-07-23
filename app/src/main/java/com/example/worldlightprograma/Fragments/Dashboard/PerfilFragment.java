package com.example.worldlightprograma.Fragments.Dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.worldlightprograma.Models.User.RptaPerfilCliente;
import com.example.worldlightprograma.Models.User.ReqEditarPerfil;
import com.example.worldlightprograma.R;
import com.example.worldlightprograma.Network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import androidx.core.content.ContextCompat;
import android.content.res.ColorStateList;

public class PerfilFragment extends Fragment {
    private AppCompatButton btnInicialesPerfil;
    private TextView tvNombreCompleto, tvEstadoPerfil, tvDni, tvFechaRegistro;
    private EditText etTelefono, etCorreo, etDireccion, etUsername, etPassword;
    private Button btnGuardarPerfil;
    private CheckBox cbHabilitarEdicion;
    private SharedPreferences sharedPreferences;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        btnInicialesPerfil = view.findViewById(R.id.btnInicialesPerfil);
        tvNombreCompleto = view.findViewById(R.id.tvNombreCompleto);
        tvEstadoPerfil = view.findViewById(R.id.tvEstadoPerfil);
        tvDni = view.findViewById(R.id.tvDni);
        tvFechaRegistro = view.findViewById(R.id.tvFechaRegistro);
        etTelefono = view.findViewById(R.id.etTelefono);
        etCorreo = view.findViewById(R.id.etCorreo);
        etDireccion = view.findViewById(R.id.etDireccion);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnGuardarPerfil = view.findViewById(R.id.btnGuardarPerfil);
        cbHabilitarEdicion = view.findViewById(R.id.cbHabilitarEdicion);
        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", android.content.Context.MODE_PRIVATE);
        progressBar = view.findViewById(R.id.progressBar);

        // Por defecto, deshabilitar edición
        setEdicionHabilitada(false);
        cbHabilitarEdicion.setOnCheckedChangeListener((buttonView, isChecked) -> setEdicionHabilitada(isChecked));

        // Inicializar contador y mostrar ProgressBar
        llamadasPendientes = 1; // solo cargar perfil
        mostrarProgressBar();
        cargarPerfil();

        btnGuardarPerfil.setOnClickListener(v -> guardarCambiosPerfil());

        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> {
            String token = sharedPreferences.getString("token", "");
            if (token.isEmpty()) {
                Toast.makeText(getContext(), "No hay token de sesión", Toast.LENGTH_LONG).show();
                return;
            }
            
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);
            apiService.logout("JWT " + token).enqueue(new Callback<com.example.worldlightprograma.Models.User.RptaRegistro>() {
                @Override
                public void onResponse(Call<com.example.worldlightprograma.Models.User.RptaRegistro> call, Response<com.example.worldlightprograma.Models.User.RptaRegistro> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                        Toast.makeText(getContext(), "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        androidx.navigation.fragment.NavHostFragment.findNavController(PerfilFragment.this)
                                .navigate(com.example.worldlightprograma.R.id.action_PerfilFragment_to_LoginFragment);
                    } else {
                        String msg = "Error al cerrar sesión";
                        if (response.body() != null) {
                            msg = response.body().getMessage();
                        } else if (!response.isSuccessful()) {
                            msg = "Error HTTP: " + response.code();
                        }
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<com.example.worldlightprograma.Models.User.RptaRegistro> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        return view;
    }

    private void cargarPerfil() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        Call<RptaPerfilCliente> call = apiService.obtenerPerfilCliente("JWT " + token);
        call.enqueue(new Callback<RptaPerfilCliente>() {
            @Override
            public void onResponse(Call<RptaPerfilCliente> call, Response<RptaPerfilCliente> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getCode() != 1) {
                    Toast.makeText(getContext(), "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show();
                    llamadaCompletada();
                    return;
                }
                RptaPerfilCliente.Data data = response.body().getData();
                String iniciales = "";
                if (!TextUtils.isEmpty(data.getNombre())) iniciales += data.getNombre().substring(0, 1).toUpperCase();
                if (!TextUtils.isEmpty(data.getApellido())) iniciales += data.getApellido().substring(0, 1).toUpperCase();
                btnInicialesPerfil.setText(iniciales);
                tvNombreCompleto.setText(data.getNombre() + " " + data.getApellido());
                tvEstadoPerfil.setText(data.getEstado());
                tvDni.setText(data.getDni());
                tvFechaRegistro.setText(data.getFecha_registro());

                etCorreo.setText(data.getCorreo());
                etTelefono.setText(data.getTelefono());
                etDireccion.setText(data.getDireccion());
                etUsername.setText(data.getUsername());
                
                llamadaCompletada();
            }
            @Override
            public void onFailure(Call<RptaPerfilCliente> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                llamadaCompletada();
            }
        });
    }

    private void guardarCambiosPerfil() {
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (correo.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        ReqEditarPerfil req = new ReqEditarPerfil(telefono, correo, direccion, username, password);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        apiService.editarPerfil("JWT " + token, req).enqueue(new Callback<RptaPerfilCliente>() {
            @Override
            public void onResponse(Call<RptaPerfilCliente> call, Response<RptaPerfilCliente> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    cargarPerfil();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RptaPerfilCliente> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEdicionHabilitada(boolean habilitado) {
        int colorFondo, colorTexto;
        if (habilitado) {
            colorFondo = ContextCompat.getColor(requireContext(), R.color.primary_blue_light);
            colorTexto = ContextCompat.getColor(requireContext(), android.R.color.black);
        } else {
            colorFondo = ContextCompat.getColor(requireContext(), R.color.disabled_background);
            colorTexto = ContextCompat.getColor(requireContext(), R.color.disabled_text);
        }
        etTelefono.setEnabled(habilitado);
        etCorreo.setEnabled(habilitado);
        etDireccion.setEnabled(habilitado);
        etUsername.setEnabled(habilitado);
        etPassword.setEnabled(habilitado);
        btnGuardarPerfil.setEnabled(habilitado);

        etTelefono.setTextColor(colorTexto);
        etCorreo.setTextColor(colorTexto);
        etDireccion.setTextColor(colorTexto);
        etUsername.setTextColor(colorTexto);
        etPassword.setTextColor(colorTexto);
        btnGuardarPerfil.setTextColor(colorTexto);
        btnGuardarPerfil.setBackgroundTintList(ColorStateList.valueOf(colorFondo));
    }
}