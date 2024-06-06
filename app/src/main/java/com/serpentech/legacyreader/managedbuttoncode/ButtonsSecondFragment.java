package com.serpentech.legacyreader.managedbuttoncode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.serpentech.legacyreader.filemanagement.ConfigFilesJson;
import com.serpentech.legacyreader.filemanagement.CustomFileManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ButtonsSecondFragment {
    public static void processFile(ActivityResultLauncher<Intent> launcher, Context context) {

        ConfigFilesJson.number = ConfigFilesJson.readBadFileNamesRecord();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        launcher.launch(intent);
    }

    public static void grabUri(Uri uri, Context context) {

        String name;
        // log the path to debug
        // Check if the real path is valid, otherwise use the URI string
        String uriPath = getRealPathFromURI(uri, context);
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

        if (ConfigFilesJson.fileInDatabase(name)) {
            // Make a Toast to inform the user that the file already exists
            Toast.makeText(context, "File already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomFileManager.copyFileUri(context, uri, name, uriPath);

    }

    public static String getRealPathFromURI(Uri contentUri, Context context) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
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

}
