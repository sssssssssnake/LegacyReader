package com.serpentech.legacyreader.filemanagement;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CustomFileManager {

    public static String appWorkingDirectory = "/storage/emulated/0/Download/LegacyReader/";


    public static void copyFileUri(Context context, Uri sourceUri, String destinationFileName, String uriPath) {
        File destinationFile;
        String fileExtension = "mxl"; // Default extension for bad files

        // Check if the file name is null (bad file) or not (good file)
        if (destinationFileName == null) {
            Log.d("CustomFileManager", "File name is null");
            // Handle bad file
            String badFileName = "badfile" + ConfigFilesJson.readBadFileNamesRecord() + "." + fileExtension;
            destinationFile = new File(appWorkingDirectory, badFileName);
            ConfigFilesJson.makeBadFileNamesRecord(ConfigFilesJson.readBadFileNamesRecord() + 1); // Increment the bad file index
        } else {
            Log.d("CustomFileManager", "File name is not null");
            // Handle good file
            File destinationFolder = new File(appWorkingDirectory, destinationFileName.substring(0, destinationFileName.lastIndexOf('.')));
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }
            destinationFile = new File(destinationFolder, destinationFileName);

            // Extract the file extension from the good file name
            int i = destinationFileName.lastIndexOf('.');
            if (i > 0) {
                fileExtension = destinationFileName.substring(i + 1);
            }
        }

        // Attempt to copy the file from the URI
        try (InputStream source = context.getContentResolver().openInputStream(sourceUri);
             OutputStream destination = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int length;
            while (true) {
                assert source != null;
                if (!((length = source.read(buf)) > 0)) break;
                destination.write(buf, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("CustomFileManager", "File copied");
        // Add the new file to the app config
        ConfigFilesJson.addConfig(destinationFile.getName(), destinationFile.getParent(), destinationFile.getAbsolutePath(), fileExtension);
        ConfigFilesJson.saveConfig();

        ExtractMxl.unzip(destinationFile.getAbsolutePath(), destinationFile.getParent());
        Log.d("CustomFileManager", "File unzipped");


//        ConfigXmlJson.addWorkingFile(new ConfigXmlJson.decompressedXmlFile());

    }

}
