package com.serpentech.legacyreader;

import static android.app.Activity.RESULT_OK;

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
                            String name;

                            // log the path to debug
                            // Check if the real path is valid, otherwise use the URI string
                            String uriPath = getRealPathFromURI(uri);
                            if (uriPath == null) {
                                uriPath = uri.toString();
                            }
                            try {
                                uriPath = URLDecoder.decode(uriPath, StandardCharsets.UTF_8.name());
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }

                            //grab the file name from the URI using last /
                            name = uriPath.substring(uriPath.lastIndexOf("/") + 1);
                            Log.d("FileStuff", "Selected file name: " + name);
                            // log path
                            Log.d("FileStuff", "Selected file path: " + uriPath);
                            // if the file name does not contain a period, nullify the field
                            if (!name.contains(".")) {
                                name = null;
                            }


                            CustomFileManager.copyFileUri(getContext(), uri, name, uriPath);                            // Now you can use the path to manipulate the file

                        }
                    }
                }
        );

        binding.buttonSecond.setOnClickListener(v -> {
            ConfigFilesJson.number = ConfigFilesJson.readBadFileNamesRecord();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            fileChooserLauncher.launch(intent);
        });

        binding.backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            // This is the case when the quick pop-up provides a content URI
            // which does not directly map to a file path
            Log.e("FileSelection", "Could not get file path: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getFilePathForN(Uri uri, Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = context.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }




}