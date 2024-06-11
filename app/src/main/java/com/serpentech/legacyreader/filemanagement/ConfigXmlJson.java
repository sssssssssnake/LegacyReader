package com.serpentech.legacyreader.filemanagement;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConfigXmlJson {
    public static List<decompressedXmlFile> workingFileList = new ArrayList<>();

    public static void writeConfigXml(List<decompressedXmlFile> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        try (FileWriter writer = new FileWriter(ConfigFilesJson.appWorkingDirectory + "workingFileList.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // the method to read the json file
    public static List<decompressedXmlFile> readConfigXml() {
        Gson gson = new Gson();
        List<decompressedXmlFile> workingFileList = new ArrayList<>();

        Type configListType = new TypeToken<List<decompressedXmlFile>>(){}.getType();

        // Read the JSON file and convert it back to a list of Java objects
        try (FileReader reader = new FileReader(ConfigFilesJson.appWorkingDirectory + "workingFileList.json")) {
            workingFileList = gson.fromJson(reader, configListType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workingFileList;
    }

    public static void addWorkingFile(decompressedXmlFile file) {
        workingFileList = readConfigXml();
        workingFileList.add(file);
        writeConfigXml(workingFileList);
    }




    public static class decompressedXmlFile {
        public String folderPath;
        public String fullPath;
        public String fileName;
        public String fileExtension;

        public decompressedXmlFile(String folderPath, String fullPath, String fileName, String fileExtension) {
            this.folderPath = folderPath;
            this.fullPath = fullPath;
            this.fileName = fileName;
            this.fileExtension = fileExtension;
        }


    }
}
