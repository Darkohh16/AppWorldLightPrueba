package com.example.worldlightprograma.Fragments.Auth;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.worldlightprograma.Models.User.ReqRegistro;
import com.example.worldlightprograma.databinding.FragmentRegistroBinding;

import java.util.Calendar;
import java.util.Locale;

public class RegistroFragment extends Fragment {

    private FragmentRegistroBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentRegistroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.btnSiguiente.setOnClickListener(v -> {

            String nombre = binding.editTextNombre.getText().toString().trim();
            String apellidos = binding.editTextApellido.getText().toString().trim();
            String dni = binding.editTextDni.getText().toString().trim();
            String genero = binding.spinnerGenero.getSelectedItem().toString();
            String correo = binding.editTextCorreo.getText().toString().trim();
            String telefono = binding.editTextTelefono.getText().toString().trim();
            String direccion = binding.editTextDireccion.getText().toString().trim();
            String fecha_nacimiento = binding.editTextFechaNacimiento.getText().toString().trim();

            if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty() || genero.isEmpty() ||
            correo.isEmpty() || telefono.isEmpty() || fecha_nacimiento.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                ReqRegistro req = new ReqRegistro();
                req.setNombre(nombre);
                req.setApellido(apellidos);
                req.setDni(dni);
                req.setGenero(genero);
                req.setCorreo(correo);
                req.setTelefono(telefono);
                req.setDireccion(direccion);
                req.setFecha_nacimiento(fecha_nacimiento);

                 RegistroFragmentDirections.ActionRegistroFragmentToMembresiaFragment action =
                    RegistroFragmentDirections.actionRegistroFragmentToMembresiaFragment(req);
                 NavHostFragment.findNavController(RegistroFragment.this)
                         .navigate(action);
            }

        });


        binding.editTextFechaNacimiento.setOnClickListener(v -> {
            // Obtener la fecha actual
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (datePicker, yearSelected, monthSelected, daySelected) -> {
                        // Formatear la fecha elegida
                        String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", yearSelected, monthSelected + 1, daySelected);
                        binding.editTextFechaNacimiento.setText(fecha);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}