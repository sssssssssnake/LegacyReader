package com.serpentech.legacyreader.filemanagement;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

public class ConfigFilesJson {

    static String appWorkingDirectory = CustomFileManager.appWorkingDirectory;

    static List<ConfigFile> configuration = new ArrayList<ConfigFile>();
    static String badFileNamesRecord = "googlebad.json";
    static public int number = 0;

    public static void addConfig(String name, String parentDirectory, String path, String extension) {
        configuration = readConfig(appWorkingDirectory);
        configuration.add(new ConfigFile(name, parentDirectory, path, extension));
    }
    public static void saveConfig() {
        writeConfig(appWorkingDirectory, configuration);
    }
    public static void writeConfig(String directoryPath, List<ConfigFile> config) {
        Gson gson = new Gson();
        String json = gson.toJson(config);

        try (FileWriter writer = new FileWriter(directoryPath + "/config.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ConfigFile> readConfig(String directoryPath) {
        Gson gson = new Gson();
        List<ConfigFile> configList = null;

        // Construct the full path to the JSON file
        String filePath = directoryPath + "/config.json";

        // Check if the file exists
        if (!Files.exists(Paths.get(filePath))) {
            System.out.println("The configuration file does not exist.");
            // Handle the case where the file does not exist
            writeConfig(directoryPath, configuration);
            return configuration;
        }

        // Define the type of the data we want to read
        Type configListType = new TypeToken<List<ConfigFile>>(){}.getType();

        // Read the JSON file and convert it back to a list of Java objects
        try (FileReader reader = new FileReader(filePath)) {
            configList = gson.fromJson(reader, configListType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return configList;
    }



    static class ConfigFile {
        String name;
        String parentDirectory;
        String path;
        String extension;

        public ConfigFile(String name, String parentDirectory, String path, String extension) {
            this.name = name;
            this.parentDirectory = parentDirectory;
            this.path = path;
            this.extension = extension;
        }
    }

    // make and read a file the represents all bad filenames using a number stored in a json file
    public static void makeBadFileNamesRecord(int number) {
        Gson gson = new Gson();
        String json = gson.toJson(number);
        try (FileWriter writer = new FileWriter(appWorkingDirectory + badFileNamesRecord)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // be able to read the number stored in a json file
    public static int readBadFileNamesRecord() {
        String filePath = appWorkingDirectory;
        Gson gson = new Gson();
        int number = 0;

        // Construct the full path to the JSON file
        String badFileNamesRecordPath = filePath + badFileNamesRecord;

        // Check if the file exists
        if (!Files.exists(Paths.get(badFileNamesRecordPath))) {
            System.out.println("The bad file names record file does not exist.");
            return 0;
        } else {
            try (FileReader reader = new FileReader(badFileNamesRecordPath)) {
                number = gson.fromJson(reader, int.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return number;
    }


}
