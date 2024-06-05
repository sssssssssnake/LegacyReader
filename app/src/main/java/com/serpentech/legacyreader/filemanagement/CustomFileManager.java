package com.serpentech.legacyreader.filemanagement;


import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CustomFileManager {

    public static String appWorkingDirectory = "/storage/emulated/0/Download/LegacyReader/";

//    public static void copyFileUri(Context context, Uri sourceUri, String destinationFileName, String uriPath) {
//        if (sourceUri != null && destinationFileName != null) {
//            File sourceFile = new File(uriPath);
//            String folderName = destinationFileName.substring(0, destinationFileName.lastIndexOf('.'));
//            File destinationFolder = new File(appWorkingDirectory + folderName);
//            if (!destinationFolder.exists()) {
//                destinationFolder.mkdirs();
//            }
//            File destinationFile = new File(destinationFolder, destinationFileName);
//
//            // Check if the uriPath is valid and if so, perform a direct file copy
//            if (sourceFile.exists()) {
//                try (FileChannel source = new FileInputStream(sourceFile).getChannel();
//                     FileChannel destination = new FileOutputStream(destinationFile).getChannel()) {
//                    destination.transferFrom(source, 0, source.size());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                // If the uriPath is not valid, use the InputStream from the URI
//                try (InputStream source = context.getContentResolver().openInputStream(sourceUri)) {
//                    try (OutputStream destination = new FileOutputStream(destinationFile)) {
//                        byte[] buf = new byte[1024];
//                        int length;
//                        while ((length = source.read(buf)) > 0) {
//                            destination.write(buf, 0, length);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            String fileExtension = "";
//            int i = destinationFileName.lastIndexOf('.');
//            if (i > 0) {
//                fileExtension = destinationFileName.substring(i+1);
//            }
//            // add the new file to the app config
//            // name, folder, path, extension
//            ConfigFilesJson.addConfig(destinationFileName, folderName, destinationFile.getAbsolutePath(), fileExtension);
//            ConfigFilesJson.saveConfig();
//        }
//    }

    public static void copyFileUri(Context context, Uri sourceUri, String destinationFileName, String uriPath) {
        File destinationFile;
        String fileExtension = "xml"; // Default extension for bad files

        // Check if the file name is null (bad file) or not (good file)
        if (destinationFileName == null) {
            // Handle bad file
            String badFileName = "badfile" + ConfigFilesJson.readBadFileNamesRecord() + "." + fileExtension;
            destinationFile = new File(appWorkingDirectory, badFileName);
            ConfigFilesJson.makeBadFileNamesRecord(ConfigFilesJson.readBadFileNamesRecord() + 1); // Increment the bad file index
        } else {
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
            while ((length = source.read(buf)) > 0) {
                destination.write(buf, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add the new file to the app config
        ConfigFilesJson.addConfig(destinationFileName, destinationFile.getParent(), destinationFile.getAbsolutePath(), fileExtension);
        ConfigFilesJson.saveConfig();
    }


//            File sourceFile = new File(uriPath);
//            String folderName = destinationFileName.substring(0, destinationFileName.lastIndexOf('.'));
//            File destinationFolder = new File(appWorkingDirectory + folderName);
//            if (!destinationFolder.exists()) {
//                destinationFolder.mkdirs();
//            }
//            File destinationFile = new File(destinationFolder, destinationFileName);
//
//            // Check if the uriPath is valid and if so, perform a direct file copy using FileReader and FileWriter
//            if (sourceFile.exists()) {
//                try (FileReader fileReader = new FileReader(sourceFile);
//                     FileWriter fileWriter = new FileWriter(destinationFile)) {
//                    int character;
//                    while ((character = fileReader.read()) != -1) {
//                        fileWriter.write(character);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                // If the uriPath is not valid, use the InputStream from the URI
//                try (InputStream source = context.getContentResolver().openInputStream(sourceUri);
//                     FileWriter fileWriter = new FileWriter(destinationFile)) {
//                    int character;
//                    while ((character = source.read()) != -1) {
//                        fileWriter.write(character);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            String fileExtension = "";
//            int i = destinationFileName.lastIndexOf('.');
//            if (i > 0) {
//                fileExtension = destinationFileName.substring(i+1);
//            }
//            // add the new file to the app config
//            // name, folder, path, extension
//            ConfigFilesJson.addConfig(destinationFileName, folderName, destinationFile.getAbsolutePath(), fileExtension);
//            ConfigFilesJson.saveConfig();
//        }
}
