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

import com.example.worldlightprograma.Models.User.ReqRecuperarCorreo;
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

public class RecuperarCorreoFragment extends androidx.fragment.app.Fragment {
    private TextInputEditText etCorreoRecuperar;
    private Button btnEnviarCodigo;
    private TextView tvMensajeRecuperar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recuperar_correo, container, false);
        etCorreoRecuperar = view.findViewById(R.id.etCorreoRecuperar);
        btnEnviarCodigo = view.findViewById(R.id.btnEnviarCodigo);
        tvMensajeRecuperar = view.findViewById(R.id.tvMensajeRecuperar);

        btnEnviarCodigo.setOnClickListener(v -> enviarCodigo());
        return view;
    }

    private void enviarCodigo() {
        String correo = etCorreoRecuperar.getText().toString().trim();
        if (TextUtils.isEmpty(correo)) {
            tvMensajeRecuperar.setText("Ingresa tu correo electrónico");
            Toast.makeText(getContext(), "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }
        tvMensajeRecuperar.setText("");
        btnEnviarCodigo.setEnabled(false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        ReqRecuperarCorreo req = new ReqRecuperarCorreo(correo);
        api.enviarCodigoRecuperacion(req).enqueue(new Callback<RptaRegistro>() {
            @Override
            public void onResponse(Call<RptaRegistro> call, Response<RptaRegistro> response) {
                btnEnviarCodigo.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    tvMensajeRecuperar.setText("Código enviado. Revisa tu correo.");
                    Toast.makeText(getContext(), "Revisa tu email", Toast.LENGTH_LONG).show();
                    Bundle args = new Bundle();
                    args.putString("correo", correo);
                    NavHostFragment.findNavController(RecuperarCorreoFragment.this)
                            .navigate(R.id.action_RecuperarCorreoFragment_to_RecuperarCodigoFragment, args);
                } else {
                    String error = response.body() != null ? response.body().getMessage() : "Error desconocido";
                    tvMensajeRecuperar.setText("Error: " + error);
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<RptaRegistro> call, Throwable t) {
                btnEnviarCodigo.setEnabled(true);
                tvMensajeRecuperar.setText("Error de conexión");
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }
}