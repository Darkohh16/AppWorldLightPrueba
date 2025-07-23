package com.example.worldlightprograma.Fragments.Auth;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.worldlightprograma.Models.Auth.ReqAuth;
import com.example.worldlightprograma.Models.Auth.RptaAuth;
import com.example.worldlightprograma.Models.Auth.RptaComprobacionMembresia;
import com.example.worldlightprograma.Models.User.ReqRegistro;
import com.example.worldlightprograma.Models.User.RptaClasesInscritas;
import com.example.worldlightprograma.Models.User.RptaRegistro;
import com.example.worldlightprograma.Network.ApiService;
import com.example.worldlightprograma.R;
import com.example.worldlightprograma.databinding.FragmentLoginBinding;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);

        binding.btnLogin.setOnClickListener(v -> {
            String usuario = binding.etUsuario.getText().toString().trim();
            String contrasena = binding.etPassword.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(getContext(), "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            obtenerToken(usuario, contrasena);
        });

        binding.tvRegistro.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(LoginFragmentDirections.actionLoginFragmentToRegistroFragment()); // Asegúrate que el ID coincida
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_LoginFragment_to_RecuperarCorreoFragment);
        });

    }

    private void obtenerToken(String p_usuario, String p_contrasena) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService wlPrograma = retrofit.create(ApiService.class);

        ReqAuth reqAuth = new ReqAuth();
        reqAuth.setUsername(p_usuario);
        reqAuth.setPassword(p_contrasena);

        Call<RptaAuth> call = wlPrograma.obtenerToken(reqAuth);
        call.enqueue(new Callback<RptaAuth>() {
            @Override
            public void onResponse(Call<RptaAuth> call, Response<RptaAuth> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getAccess_token() == null || response.body().getAccess_token().isEmpty()) {
                    Toast.makeText(getActivity(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    return;
                }


                String token = response.body().getAccess_token();

                // Guardar token en SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", token);
                editor.apply();

                // Mostrar éxito
                Toast.makeText(getActivity(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                // Ir a la siguiente pantalla
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(LoginFragmentDirections.actionLoginFragmentToPrincipalFragment());
            }

            @Override
            public void onFailure(Call<RptaAuth> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
