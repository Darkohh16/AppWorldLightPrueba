package com.example.worldlightprograma.Fragments.Auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.worldlightprograma.Models.User.ReqRegistro;
import com.example.worldlightprograma.databinding.FragmentMembresiaBinding;

public class MembresiaFragment extends Fragment {

    private FragmentMembresiaBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMembresiaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.btnWorld.setOnClickListener(v -> {

            ReqRegistro req = MembresiaFragmentArgs.fromBundle(getArguments()).getReqRegistro();
            req.setMembresia_id(2);

          MembresiaFragmentDirections.ActionMembresiaFragmentToRegistroUserFragment action =
                  MembresiaFragmentDirections.actionMembresiaFragmentToRegistroUserFragment(req);
            NavHostFragment.findNavController(MembresiaFragment.this)
                    .navigate(action);

        });

        binding.btnBlue.setOnClickListener(v -> {

            ReqRegistro req = MembresiaFragmentArgs.fromBundle(getArguments()).getReqRegistro();
            req.setMembresia_id(1);

            MembresiaFragmentDirections.ActionMembresiaFragmentToRegistroUserFragment action =
                    MembresiaFragmentDirections.actionMembresiaFragmentToRegistroUserFragment(req);
            NavHostFragment.findNavController(MembresiaFragment.this)
                    .navigate(action);

        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}