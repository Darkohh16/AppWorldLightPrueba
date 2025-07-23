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

import com.example.worldlightprograma.Models.User.ReqRecuperarCodigo;
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

public class RecuperarCodigoFragment extends androidx.fragment.app.Fragment {
    private TextInputEditText etCodigo;
    private Button btnVerificarCodigo;
    private TextView tvMensajeCodigo;
    private String correo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recuperar_codigo, container, false);
        etCodigo = view.findViewById(R.id.etCodigo);
        btnVerificarCodigo = view.findViewById(R.id.btnVerificarCodigo);
        tvMensajeCodigo = view.findViewById(R.id.tvMensajeCodigo);
        if (getArguments() != null) {
            correo = getArguments().getString("correo", "");
        }
        btnVerificarCodigo.setOnClickListener(v -> verificarCodigo());
        return view;
    }

    private void verificarCodigo() {
        String codigo = etCodigo.getText().toString().trim();
        if (TextUtils.isEmpty(codigo) || codigo.length() != 6) {
            tvMensajeCodigo.setText("Ingresa el código de 6 dígitos");
            Toast.makeText(getContext(), "Ingresa el código de 6 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }
        tvMensajeCodigo.setText("");
        btnVerificarCodigo.setEnabled(false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        ReqRecuperarCodigo req = new ReqRecuperarCodigo(correo, codigo);
        api.verificarCodigoRecuperacion(req).enqueue(new Callback<RptaRegistro>() {
            @Override
            public void onResponse(Call<RptaRegistro> call, Response<RptaRegistro> response) {
                btnVerificarCodigo.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1) {
                    Toast.makeText(getContext(), "Código correcto", Toast.LENGTH_LONG).show();
                    Bundle args = new Bundle();
                    args.putString("correo", correo);
                    args.putString("codigo", codigo);
                    NavHostFragment.findNavController(RecuperarCodigoFragment.this)
                            .navigate(R.id.action_RecuperarCodigoFragment_to_RecuperarNuevaContrasenaFragment, args);
                } else {
                    String error = response.body() != null ? response.body().getMessage() : "Código incorrecto o expirado";
                    tvMensajeCodigo.setText("Código incorrecto o expirado");
                    Toast.makeText(getContext(), "Código incorrecto o expirado", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<RptaRegistro> call, Throwable t) {
                btnVerificarCodigo.setEnabled(true);
                tvMensajeCodigo.setText("Error de conexión");
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }
} 