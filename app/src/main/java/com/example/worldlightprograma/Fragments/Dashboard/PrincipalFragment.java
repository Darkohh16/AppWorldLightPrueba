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
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldlightprograma.Fragments.Auth.LoginFragment;
import com.example.worldlightprograma.Fragments.Auth.LoginFragmentDirections;
import com.example.worldlightprograma.Models.Auth.ReqAuth;
import com.example.worldlightprograma.Models.Auth.RptaAuth;
import com.example.worldlightprograma.Models.Auth.RptaComprobacionMembresia;
import com.example.worldlightprograma.Models.User.RptaClasesInscritas;
import com.example.worldlightprograma.Models.User.RptaDatosPrincipal;
import com.example.worldlightprograma.Models.User.RptaRutinaActiva;
import com.example.worldlightprograma.Network.ApiService;
import com.example.worldlightprograma.R;
import com.example.worldlightprograma.databinding.FragmentLoginBinding;
import com.example.worldlightprograma.databinding.FragmentPrincipalBinding;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PrincipalFragment extends Fragment {

    private FragmentPrincipalBinding binding;
    SharedPreferences sharedPreferences;
    private RptaDatosPrincipal.datos datosUsuario;
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
        binding = FragmentPrincipalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Inicializar contador y mostrar ProgressBar
        llamadasPendientes = 3; // 3 llamadas: data principal, membresia, clases
        mostrarProgressBar();

        //DATA PRINCIPAL (CARD DE INFO)
        llamarDataPrincipal();

        //Comprobar membresia
        comprobarMembresia();

        //DATA CLASES
        llamarClases();

        //RECARGAR VISTA
        binding.btnRecarga.setOnClickListener(v -> {
            llamadasPendientes = 2; // Solo data principal y clases
            mostrarProgressBar();
            llamarDataPrincipal();
            llamarClases();
        });

        binding.btnPlanNutricional.setOnClickListener(v -> {
            if (datosUsuario != null) {
                PrincipalFragmentDirections.ActionPrincipalFragmentToPlanNutricionalFragment action =
                    PrincipalFragmentDirections.actionPrincipalFragmentToPlanNutricionalFragment(
                        datosUsuario.getNombre(), datosUsuario.getApellido()
                    );
                NavHostFragment.findNavController(PrincipalFragment.this).navigate(action);
            } else {
                Toast.makeText(getContext(), "Datos de usuario no disponibles", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnRutinaPersonalizada.setOnClickListener(v -> {
            comprobarRutinaActiva();
        });

        binding.btnVerClasesDisponibles.setOnClickListener(v -> {
            NavHostFragment.findNavController(PrincipalFragment.this)
                .navigate(PrincipalFragmentDirections.actionPrincipalFragmentToClasesDisponiblesFragment());
        });

        // Navegación a PerfilFragment al presionar el círculo de iniciales
        binding.btnInicialesUsuario.setOnClickListener(v -> {
            NavHostFragment.findNavController(PrincipalFragment.this)
                .navigate(R.id.action_PrincipalFragment_to_PerfilFragment);
        });

    }

    private void llamarClases() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService wLPrograma = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        Call<RptaClasesInscritas> call = wLPrograma.obtenerClasesInscritas("JWT " + token);

        call.enqueue(new Callback<RptaClasesInscritas>() {
            @Override
            public void onResponse(Call<RptaClasesInscritas> call, Response<RptaClasesInscritas> response) {
                if (!response.isSuccessful()) {
                    llamadaCompletada();
                    return;
                }

                RptaClasesInscritas rpta = response.body();
                if (rpta.getCode() == 1) {
                    List<RptaClasesInscritas.datos> clases = rpta.getData();

                    //Limpianding
                    binding.llClasesContainer.removeAllViews();

                    if (clases.isEmpty()) {
                        binding.tvSinClases.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvSinClases.setVisibility(View.GONE);
                        LayoutInflater inflater = LayoutInflater.from(getContext());

                        for (RptaClasesInscritas.datos clasesInscritas : clases) {
                            View cardView = inflater.inflate(R.layout.item_clase, binding.llClasesContainer, false);

                            TextView tvHora = cardView.findViewById(R.id.tvHora);
                            TextView tvClase = cardView.findViewById(R.id.tvClase);
                            TextView tvProfesor = cardView.findViewById(R.id.tvInstructor);
                            TextView tvDuracion = cardView.findViewById(R.id.tvDuracion);

                            String horaInicio = clasesInscritas.getHora_inicio().substring(0, 5);
                            String horaFin = clasesInscritas.getHora_fin().substring(0, 5);
                            int duracion = calcularMinutos(horaInicio, horaFin);

                            tvHora.setText(formatearHora(horaInicio));
                            tvClase.setText(clasesInscritas.getClase());
                            tvProfesor.setText(clasesInscritas.getInstructor());
                            tvDuracion.setText(duracion + " min");

                            binding.llClasesContainer.addView(cardView);
                        }
                    }
                }
                llamadaCompletada();
            }

            @Override
            public void onFailure(Call<RptaClasesInscritas> call, Throwable t) {
                Toast.makeText(getContext(), "Error al obtener clases", Toast.LENGTH_SHORT).show();
                llamadaCompletada();
            }
        });
    }

    private int calcularMinutos(String horaInicio, String horaFin) {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date inicio = formato.parse(horaInicio);
            Date fin = formato.parse(horaFin);

            long diferenciaMs = fin.getTime() - inicio.getTime();
            return (int) (diferenciaMs / (1000 * 60));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String formatearHora(String hora24) {
        try {
            SimpleDateFormat formato24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat formato12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date hora = formato24.parse(hora24);
            return formato12.format(hora);
        } catch (Exception e) {
            e.printStackTrace();
            return hora24;
        }
    }

    private void comprobarMembresia() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService wLPrograma = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        Call<RptaComprobacionMembresia> call = wLPrograma.obtenerComprobacion("JWT " + token);

        call.enqueue(new Callback<RptaComprobacionMembresia>() {
            @Override
            public void onResponse(Call<RptaComprobacionMembresia> call, Response<RptaComprobacionMembresia> response) {
                if (!response.isSuccessful()) {
                    llamadaCompletada();
                    return;
                }

                RptaComprobacionMembresia rpta = response.body();
                if (rpta.getCode() == 0) {

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Alerta")
                            .setMessage("Su membresia no esta activa, renueve para poder continuar...\n" +
                                    "\n" +
                                    "\n" +
                                    "\n" +
                                    "\n" +
                                    "*Si es un colaborador, comuniquese con un administrador*")
                            .setCancelable(false)
                            .setPositiveButton("Confirmar", (dialog, which) -> {
                                dialog.dismiss();

                                // Redirigir al Login después de confirmar
                                NavHostFragment.findNavController(PrincipalFragment.this)
                                        .navigate(PrincipalFragmentDirections.actionPrincipalFragmentToLoginFragment());
                            })
                            .show();
                }
                llamadaCompletada();
            }

            @Override
            public void onFailure(Call<RptaComprobacionMembresia> call, Throwable t) {
                Toast.makeText(getContext(), "Error interno.", Toast.LENGTH_SHORT).show();
                llamadaCompletada();
            }
        });

    }

    private void llamarDataPrincipal() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService wlPrograma = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        Call<RptaDatosPrincipal> call = wlPrograma.obtenerDataPrincipal("JWT " + token);
        call.enqueue(new Callback<RptaDatosPrincipal>() {
            @Override
            public void onResponse(Call<RptaDatosPrincipal> call, Response<RptaDatosPrincipal> response) {
                if(!response.isSuccessful()){
                    llamadaCompletada();
                    return;
                }
                RptaDatosPrincipal rptaObtenerData = response.body();
                int code = rptaObtenerData.getCode();
                String message = rptaObtenerData.getMessage();
                RptaDatosPrincipal.datos data = rptaObtenerData.getData();
                datosUsuario = data;
                String fechaActual = fechaActual();
                binding.tvSaludo.setText("¡Hola, " + data.getNombre() + " " + data.getApellido() + "!");
                binding.tvTipo.setText(data.getEstado().toUpperCase());
                binding.tvVencimiento.setText("Vence: " + data.getFecha_vencimiento());
                binding.tvActiva.setText(data.getMembresia().toUpperCase());
                binding.tvFecha.setText(fechaActual);

                // Iniciales del usuario
                String iniciales = "";
                if (data.getNombre() != null && !data.getNombre().isEmpty()) {
                    iniciales += data.getNombre().substring(0, 1).toUpperCase();
                }
                if (data.getApellido() != null && !data.getApellido().isEmpty()) {
                    iniciales += data.getApellido().substring(0, 1).toUpperCase();
                }
                binding.btnInicialesUsuario.setText(iniciales);
                
                llamadaCompletada();
            }

            @Override
            public void onFailure(Call<RptaDatosPrincipal> call, Throwable t) {
                llamadaCompletada();
            }
        });
    }

    private void comprobarRutinaActiva() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://proyectoworldlight.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService wLPrograma = retrofit.create(ApiService.class);
        String token = sharedPreferences.getString("token", "");
        wLPrograma.obtenerRutinaActiva("JWT " + token).enqueue(new Callback<com.example.worldlightprograma.Models.User.RptaRutinaActiva>() {
            @Override
            public void onResponse(Call<com.example.worldlightprograma.Models.User.RptaRutinaActiva> call, Response<com.example.worldlightprograma.Models.User.RptaRutinaActiva> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.worldlightprograma.Models.User.RptaRutinaActiva rpta = response.body();
                    if (rpta.getCode() == 1) {
                        // Hay rutina activa
                        NavHostFragment.findNavController(PrincipalFragment.this)
                                .navigate(PrincipalFragmentDirections.actionPrincipalFragmentToRutinasFragment());
                    } else {
                        // No hay rutina activa
                        NavHostFragment.findNavController(PrincipalFragment.this)
                                .navigate(R.id.action_PrincipalFragment_to_CrearRutinaFragment);
                    }
                } else {
                    // No hay rutina activa (404 o code != 1)
                    NavHostFragment.findNavController(PrincipalFragment.this)
                            .navigate(R.id.action_PrincipalFragment_to_CrearRutinaFragment);
                }
            }
            @Override
            public void onFailure(Call<com.example.worldlightprograma.Models.User.RptaRutinaActiva> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String fechaActual(){
        Date hoy = new Date();

        // Ejemplo: "martes, 8 de julio"
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d 'de' MMMM", new Locale("es", "ES"));
        String fecha = sdf.format(hoy);

        // Capitalizar primera letra (opcional)
        fecha = fecha.substring(0, 1).toUpperCase() + fecha.substring(1);

        return fecha;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}