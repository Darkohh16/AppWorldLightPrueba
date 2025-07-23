package com.example.worldlightprograma.Fragments.Auth;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.worldlightprograma.Models.User.ReqRegistro;
import com.example.worldlightprograma.Models.User.RptaRegistro;
import com.example.worldlightprograma.Network.ApiService;
import com.example.worldlightprograma.databinding.FragmentRegistroUserBinding;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroUserFragment extends Fragment {

    private FragmentRegistroUserBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentRegistroUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSiguiente.setOnClickListener(v -> {
            String usuario = binding.etUsuario.getText().toString().trim();
            String contrasena = binding.etPassword.getText().toString().trim();
            String confirm_contrasena = binding.etConfirmPassword.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty() || confirm_contrasena.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            } else if (!contrasena.equals(confirm_contrasena)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            } else {
                ReqRegistro req = MembresiaFragmentArgs.fromBundle(getArguments()).getReqRegistro();

                req.setUsername(usuario);
                req.setPassword(contrasena);
                req.setMetodo_pago("efectivo");
                registrarCliente(req);
            }


        });

    }

    private void registrarCliente(ReqRegistro req) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<RptaRegistro> call = apiService.registrarCliente(req);

        call.enqueue(new Callback<RptaRegistro>() {
            @Override
            public void onResponse(Call<RptaRegistro> call, Response<RptaRegistro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RptaRegistro respuesta = response.body();
                    if (respuesta.getCode() == 1) {
                        Toast.makeText(getActivity(), "Registro exitoso. Accede para comenzar tu cambio físico.",
                                Toast.LENGTH_SHORT).show();

                        NavHostFragment.findNavController(RegistroUserFragment.this)
                                .navigate(RegistroUserFragmentDirections.actionRegistroUserFragmentToLoginFragment());
                    } else {
                        mostrarMensajeDialogo("Error al registrar", respuesta.getMessage());
                    }
                } else {
                    String mensajeError = "Error inesperado del servidor";
                    if (response.errorBody() != null) {
                        try {
                            mensajeError = response.errorBody().string();
                        } catch (IOException e) {
                            mensajeError = "Error al leer el mensaje del servidor: " + e.getMessage();
                        }
                    }
                    mostrarMensajeDialogo("Error de red o servidor", mensajeError);
                }
            }



            private void mostrarMensajeDialogo(String titulo, String mensaje) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(titulo)
                        .setMessage(mensaje)
                        .setPositiveButton("OK", null)
                        .show();
            }




            @Override
            public void onFailure(Call<RptaRegistro> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de red: "
                        + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}