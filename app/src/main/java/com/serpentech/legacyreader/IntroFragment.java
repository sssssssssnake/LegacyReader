package com.serpentech.legacyreader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.serpentech.legacyreader.databinding.FragmentIntroBinding;
import com.serpentech.legacyreader.musicstuff.MusicSheetView;

public class IntroFragment extends Fragment {

    private FragmentIntroBinding binding;
    private MusicSheetView musicSheetView;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        binding = FragmentIntroBinding.inflate(inflater, container, false);


        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(v ->
                NavHostFragment.findNavController(IntroFragment.this)
                        .navigate(R.id.action_IntroFragment_to_SecondFragment)
        );
        if (MainActivity.appropriatePermissions) {
            // Corrected navigation call
            NavHostFragment.findNavController(IntroFragment.this)
                    .navigate(R.id.action_IntroFragment_to_SecondFragment);
        } else if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED )&& (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)) {
            NavHostFragment.findNavController(IntroFragment.this)
                    .navigate(R.id.action_IntroFragment_to_SecondFragment);

        }
        Log.d("IntroFragment", "onViewCreated: " + MainActivity.appropriatePermissions);
        Log.d("IntroFragment", "onViewCreated: " + ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE));
        Log.d("IntroFragment", "onViewCreated: " + ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}