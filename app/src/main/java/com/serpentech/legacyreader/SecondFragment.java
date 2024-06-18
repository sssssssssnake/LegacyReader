package com.serpentech.legacyreader;

import static android.app.Activity.RESULT_OK;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.serpentech.legacyreader.databinding.FragmentSecondBinding;
import com.serpentech.legacyreader.filemanagement.ConfigFilesJson;
import com.serpentech.legacyreader.filemanagement.CustomFileManager;
import com.serpentech.legacyreader.managedbuttoncode.ButtonsSecondFragment;


public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private ActivityResultLauncher<Intent> fileChooserLauncher;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            assert uri != null;
                            try {
                                ButtonsSecondFragment.grabUri(uri, getContext());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // Now you can use the path to manipulate the file

                        }
                    }
                }
        );

        binding.buttonSecond.setOnClickListener(v -> {


            ButtonsSecondFragment.processFile(fileChooserLauncher, getContext());
        });

        binding.backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_IntroFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }








}