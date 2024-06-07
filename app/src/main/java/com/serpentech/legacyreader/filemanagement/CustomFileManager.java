package com.serpentech.legacyreader.filemanagement;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.serpentech.legacyreader.filemanagement.xmlmanage.ExtractMxl;
import com.serpentech.legacyreader.filemanagement.xmlmanage.XmlGrab;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CustomFileManager {

    public static String appWorkingDirectory = "/storage/emulated/0/Download/LegacyReader/";


    public static void copyFileUri(Context context, Uri sourceUri, String destinationFileName, String uriPath) throws IOException {
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

        // look at the extension of the file and unzip it
        if (fileExtension.equals("mxl")) {

            ExtractMxl.unzip(destinationFile.getAbsolutePath(), destinationFile.getParent());
            Log.d("CustomFileManager", "File unzipped");

            // Add the new file to the working directory
            String mxlConfigurationContent = new String(Files.readAllBytes(Paths.get(destinationFile.getParent() + "/META-INF/container.xml")));
            XmlGrab xmlGrab = new XmlGrab();
            xmlGrab.xmlGroups = xmlGrab.scanXMLForKeywordList(mxlConfigurationContent, "rootfiles");
            List<String[]> rootFiles;
            rootFiles = xmlGrab.grabHeaders(xmlGrab.xmlGroups.get(0).contents, "rootfile");
            Log.d("CustomFileManager", rootFiles.get(0)[1] + " yep got it");
            if (rootFiles.get(0)[1].contains("/")) {
                ConfigXmlJson.addWorkingFile(new ConfigXmlJson.decompressedXmlFile(
                        rootFiles.get(0)[1].substring(0, rootFiles.get(0)[1].lastIndexOf('/')),
                        destinationFile.getAbsolutePath() + "/" + rootFiles.get(0)[1],
                        // file name
                        rootFiles.get(0)[1].substring(rootFiles.get(0)[1].lastIndexOf('/') + 1, rootFiles.get(0)[1].lastIndexOf('.')),
                        // file extension
                        rootFiles.get(0)[1].substring(rootFiles.get(0)[1].lastIndexOf('.') + 1)
                ));
            } else {
                ConfigXmlJson.addWorkingFile(new ConfigXmlJson.decompressedXmlFile(
                        "",
                        destinationFile.getAbsolutePath() + "/" + rootFiles.get(0)[1],
                        // file name
                        rootFiles.get(0)[1].substring(0, rootFiles.get(0)[1].lastIndexOf('.')),
                        // file extension
                        rootFiles.get(0)[1].substring(rootFiles.get(0)[1].lastIndexOf('.') + 1)
                ));
            }
        } else {
            Log.d("CustomFileManager", "bad filetype");
        }

    }

}
