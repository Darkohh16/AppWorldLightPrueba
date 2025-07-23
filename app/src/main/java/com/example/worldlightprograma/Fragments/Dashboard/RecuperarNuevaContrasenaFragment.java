package com.example.worldlightprograma.Fragments.Dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.example.worldlightprograma.Models.User.ReqRecuperarNuevaContrasena;
import com.example.worldlightprograma.Models.User.RptaRegistro;
import com.example.worldlightprograma.Network.ApiService;
import com.example.worldlightprograma.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class RecuperarNuevaContrasenaFragment extends androidx.fragment.app.Fragment {
    private TextInputEditText etNuevaContrasena, etConfirmarContrasena;
    private Button btnGuardarNuevaContrasena;
    private TextView tvMensajeNuevaContrasena;
    private String correo, codigo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recuperar_nueva_contrasena, container, false);
        etNuevaContrasena = view.findViewById(R.id.etNuevaContrasena);
        etConfirmarContrasena = view.findViewById(R.id.etConfirmarContrasena);
        btnGuardarNuevaContrasena = view.findViewById(R.id.btnGuardarNuevaContrasena);
        tvMensajeNuevaContrasena = view.findViewById(R.id.tvMensajeNuevaContrasena);
        if (getArguments() != null) {
            correo = getArguments().getString("correo", "");
            codigo = getArguments().getString("codigo", "");
        }
        btnGuardarNuevaContrasena.setOnClickListener(v -> guardarNuevaContrasena());
        return view;
    }

    private void guardarNuevaContrasena() {
        String nueva = etNuevaContrasena.getText().toString().trim();
        String confirmar = etConfirmarContrasena.getText().toString().trim();
        if (TextUtils.isEmpty(nueva) || TextUtils.isEmpty(confirmar)) {
            tvMensajeNuevaContrasena.setText("Completa ambos campos");
            Toast.makeText(getContext(), "Completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nueva.equals(confirmar)) {
            tvMensajeNuevaContrasena.setText("Las contraseñas no coinciden");
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        tvMensajeNuevaContrasena.setText("");
        btnGuardarNuevaContrasena.setEnabled(false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        ReqRecuperarNuevaContrasena req = new ReqRecuperarNuevaContrasena(correo, codigo, nueva);
        api.nuevaContrasenaRecuperacion(req).enqueue(new Callback<RptaRegistro>() {
            @Override
            public void onResponse(Call<RptaRegistro> call, Response<RptaRegistro> response) {
                btnGuardarNuevaContrasena.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    tvMensajeNuevaContrasena.setText("Contraseña actualizada. Inicia sesión.");
                    Toast.makeText(getContext(), "Cambio de contraseña exitoso", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(RecuperarNuevaContrasenaFragment.this)
                            .navigate(R.id.action_RecuperarNuevaContrasenaFragment_to_LoginFragment);
                } else {
                    String error = response.body() != null ? response.body().getMessage() : "Error desconocido";
                    tvMensajeNuevaContrasena.setText("Error: " + error);
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<RptaRegistro> call, Throwable t) {
                btnGuardarNuevaContrasena.setEnabled(true);
                tvMensajeNuevaContrasena.setText("Error de conexión");
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }
} 