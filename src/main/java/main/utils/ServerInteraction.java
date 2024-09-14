package main.utils;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import main.state.IStateManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerInteraction {
    private static final String SERVER_URL = "http://localhost:3000";
    private final IStateManager stateManager;

    @Inject
    public ServerInteraction(IStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public static void sendPostRequest(String endpoint, String jsonFileName, String jsonContent, String metadataFileName, String metadataContent) throws IOException {
        URL url = new URL(SERVER_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("jsonFileName", jsonFileName);
            jsonObject.addProperty("jsonContent", jsonContent);
            jsonObject.addProperty("metadataFileName", metadataFileName);
            jsonObject.addProperty("metadataContent", metadataContent);

            writer.write(jsonObject.toString());
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder responseStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }
            String responseMessage = responseStringBuilder.toString();
            System.out.println(responseMessage);
            connection.disconnect();
        } else {
            System.out.println("Failed to create file on the server. Response Code: " + responseCode);
            connection.disconnect();
        }
        connection.disconnect();
    }

    public static List<String> getFileList() throws IOException {
        URL url = new URL(SERVER_URL + "/get-file-list");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder responseStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }
            String response = responseStringBuilder.toString();
            Gson gson = new Gson();
            FileListResponse fileListResponse = gson.fromJson(response, FileListResponse.class);

            if (fileListResponse != null) {
                List<String> fileList = fileListResponse.getFiles();
                connection.disconnect();
                return fileList;
            } else {
                System.out.println("Failed to parse file list response.");
                connection.disconnect();
                return Collections.emptyList();
            }
        } else {
            System.out.println("Failed to get file list from the server. Response Code: " + responseCode);
            connection.disconnect();
            return Collections.emptyList();
        }
    }


    public class FileListResponse {
        private List<String> files;

        public List<String> getFiles() {
            return files;
        }
    }

    public static Map<String, String> getFileContent(String fileName) throws IOException {
        Map<String, String> fileContents = new HashMap<>();
        String requestUrl = SERVER_URL + "/get-file-content?fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder responseStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseStringBuilder.toString(), JsonObject.class);
            String jsonContent = jsonObject.getAsJsonPrimitive("jsonContent").getAsString();
            String metadataContent = jsonObject.getAsJsonPrimitive("metadataContent").getAsString();
            fileContents.put("jsonContent", jsonContent);
            fileContents.put("metadataContent", metadataContent);
            connection.disconnect();
        } else {
            System.out.println("Failed to get file content from the server. Response Code: " + responseCode);
            connection.disconnect();
        }

        return fileContents;
    }
}
