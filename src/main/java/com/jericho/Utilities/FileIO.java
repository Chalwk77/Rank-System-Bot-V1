// Copyright (c) 2022, Jericho Crosby <jericho.crosby227@gmail.com>

package com.jericho.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class FileIO {

    /**
     * Get the path of the program.
     *
     * @return the path of the program.
     */
    public static String getProgramPath() {
        String currentDirectory = System.getProperty("user.dir");
        currentDirectory = currentDirectory.replace("\\", "/");
        return currentDirectory;
    }

    /**
     * Load a JSON file.
     *
     * @param file the name of the file.
     * @return the JSON object.
     */
    public static JSONObject loadJSONObject(String file) throws IOException {
        String d = getProgramPath();
        File f = new File(d + "/" + file);

        BufferedReader reader = new BufferedReader(new FileReader(f));

        String line = reader.readLine();
        StringBuilder stringBuilder = new StringBuilder();

        while (line != null) {
            stringBuilder.append(line);
            line = reader.readLine();
        }

        String content = stringBuilder.toString();
        if (content.equals("")) {
            return new JSONObject();
        } else {
            return new JSONObject(content);
        }
    }

    public static JSONObject loadJSONObjectFromDir(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();
        StringBuilder stringBuilder = new StringBuilder();

        while (line != null) {
            stringBuilder.append(line);
            line = reader.readLine();
        }

        String content = stringBuilder.toString();
        if (content.equals("")) {
            return new JSONObject();
        } else {
            return new JSONObject(content);
        }
    }


    /**
     * Write a JSON file.
     *
     * @param file the name of the file.
     */
    public static void writeJSONObject(JSONObject json, String file) throws IOException {
        String d = getProgramPath();
        FileWriter fileWriter = new FileWriter(d + "/" + file);
        fileWriter.write(json.toString(4));
        fileWriter.flush();
        fileWriter.close();
    }
}
