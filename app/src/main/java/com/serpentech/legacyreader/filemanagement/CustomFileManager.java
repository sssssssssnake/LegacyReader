package com.serpentech.legacyreader.filemanagement;


import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CustomFileManager {

    public static String appWorkingDirectory = "/storage/emulated/0/Download/LegacyReader/";

    public static void copyFileUri(Uri sourceUri, String destinationFileName) {
        if (sourceUri != null && destinationFileName != null) {
            File sourceFile = new File(sourceUri.getPath());
            // Remove the file extension from the destination file name
            String folderName = destinationFileName.substring(0, destinationFileName.lastIndexOf('.'));
            File destinationFolder = new File(appWorkingDirectory + folderName);
            // Create the folder if it doesn't exist
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }
            File destinationFile = new File(destinationFolder, destinationFileName);
            try (FileChannel source = new FileInputStream(sourceFile).getChannel();
                 FileChannel destination = new FileOutputStream(destinationFile).getChannel()) {
                destination.transferFrom(source, 0, source.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
