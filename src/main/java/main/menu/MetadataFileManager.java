package main.menu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import main.body.validator.IMessageDisplay;
import main.state.IStateManager;
import main.state.ISubscriber;
import main.state.State;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MetadataFileManager implements IMetadataFileManager, ISubscriber {
    private final IStateManager stateManager;
    private IMessageDisplay messageDisplay;

    @Inject
    public MetadataFileManager(IStateManager stateManager, IMessageDisplay messageDisplay) {
        this.stateManager = stateManager;
        this.messageDisplay = messageDisplay;
    }

    @Override
    public void create() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("JSON Files", "json");
        fileChooser.setFileFilter(jsonFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File("."));

        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String filePathStr = String.valueOf(fileChooser.getSelectedFile());
            if (!filePathStr.endsWith(".json")) {
                filePathStr += ".json";
            }
            File filePath = new File(filePathStr);
            String fileName = fileChooser.getName(filePath);
            String metadataFileName = generateMetadataFileName(fileName);
            Path metadataFilePath = Paths.get(filePath.getParent(), metadataFileName);

            State state = this.stateManager.getState();
            state.json = "";
            state.metadata = new HashMap<>();
            state.currentJsonFile = fileName;
            state.currentJsonFilePath = Paths.get(filePath.getAbsolutePath());
            state.currentMetadataFile = metadataFileName;
            state.currentMetadataFilePath = metadataFilePath;

            try {
                Files.write(state.currentJsonFilePath, state.json.getBytes());
                Files.write(state.currentMetadataFilePath, "".getBytes());

                this.stateManager.update();
                JOptionPane.showMessageDialog(null, "Файл створено на пристрої", "Файл створено", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateMetadataFileName(String jsonFileName) {
        return "metadata_" +  jsonFileName.substring(0, jsonFileName.lastIndexOf(".")) + ".txt";
    }

    @Override
    public void open(String filePath) {
        State state = this.stateManager.getState();
        state.metadata = new HashMap<>();
        Path jsonPath = Paths.get(filePath);
        Path metadataPath = findMetadataFile(jsonPath);

        if (metadataPath != null) {
            state.currentMetadataFilePath = metadataPath;
            state.currentMetadataFile = metadataPath.getFileName().toString();

            this.stateManager.update();
        } else {
            generateMetadataFile(jsonPath);
            open(filePath);
        }
    }

    private void generateMetadataFile(Path jsonPath) {
        try {
            byte[] jsonBytes = Files.readAllBytes(jsonPath);
            String jsonContent = new String(jsonBytes);
            Map<String, String> metadata = generateMetadataFromJson(jsonContent);
            String metadataFileName = generateMetadataFileName(jsonPath.getFileName().toString());
            Path metadataFilePath = Paths.get(jsonPath.getParent().toString(), metadataFileName);
            Files.write(metadataFilePath, metadata.entrySet().toString().getBytes());
            this.messageDisplay.showSuccess("Файл метаданих згенеровано: " + metadataFileName);
            State state = this.stateManager.getState();
            state.currentMetadataFilePath = metadataFilePath;
            state.currentMetadataFile = metadataFileName;

            this.stateManager.update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> generateMetadataFromJson(String jsonContent) {
        Map<String, String> metadata = new HashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            processJsonNode(jsonNode, "", metadata);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return metadata;
    }

    private void processJsonNode(JsonNode jsonNode, String parentPath, Map<String, String> metadata) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                JsonNode fieldValue = field.getValue();

                String fullPath = parentPath.isEmpty() ? fieldName : parentPath + "." + fieldName;

                if (fieldValue.isObject() || fieldValue.isContainerNode()) {
                    metadata.put(fullPath, "object");
                    processJsonNode(fieldValue, fullPath, metadata);
                } else if (fieldValue.isBoolean()) {
                    metadata.put(fullPath, "boolean");
                } else if (fieldValue.isNumber()) {
                    metadata.put(fullPath, "number");
                } else if (fieldValue.isTextual()) {
                    metadata.put(fullPath, "string");
                }
            }
        }
    }

    private Path findMetadataFile(Path jsonFilePath) {
        String metadataFileName = generateMetadataFileName(jsonFilePath.getFileName().toString());

        Path metadataPath = Paths.get(jsonFilePath.getParent().toString(), metadataFileName);

        if (Files.exists(metadataPath)) {
            loadMetadataFromFile(metadataPath);
            return metadataPath;
        } else {
            return null;
        }
    }

    public void loadMetadataFromFile(Path metadataFilePath) {
        try {
            String metadataFileContent = Files.readString(metadataFilePath);
            loadMetadataFromFile(metadataFileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadMetadataFromFile(String metadataFileContent) {
        String[] metadataPairs = metadataFileContent.replaceAll("[\\[\\]]", "").split(", ");

        for (String pair : metadataPairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                stateManager.getState().metadata.put(key, value);
            }
        }
    }

    @Override
    public void save() {
        try {
            Path metadataFilePath = stateManager.getState().currentMetadataFilePath;

            if (metadataFilePath != null) {
                RandomAccessFile file = new RandomAccessFile(metadataFilePath.toFile(), "rw");
                file.setLength(0);

                file.write("[".getBytes());
                for (Map.Entry<String, String> entry : stateManager.getState().metadata.entrySet()) {
                    file.write((entry.getKey() + "=" + entry.getValue() + ", ").getBytes());
                }
                if (!stateManager.getState().metadata.isEmpty()) {
                    file.seek(file.length() - 2);
                }
                file.write("]".getBytes());
                file.close();
            } else {
                JOptionPane.showMessageDialog(null, "Шлях до файлу метаданих не встановлено", "Неможливо зберегти", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
    }
}
