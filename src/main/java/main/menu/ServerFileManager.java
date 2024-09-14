package main.menu;

import com.google.inject.Inject;
import main.state.IStateManager;
import main.state.ISubscriber;
import main.state.State;
import main.utils.ServerInteraction;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerFileManager implements IServerFileManager, ISubscriber {
    private final IStateManager stateManager;
    private final IMetadataFileManager metadataFileManager;

    @Inject
    public ServerFileManager(IStateManager stateManager, IMetadataFileManager metadataFileManager) {
        this.metadataFileManager = metadataFileManager;
        stateManager.subscribe(this);
        this.stateManager = stateManager;
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
            String metadataName = fileName.substring(0, fileName.lastIndexOf(".")) + ".txt";

            String metadataFileName = generateMetadataFileName(metadataName);

            State state = this.stateManager.getState();
            state.json = "";
            state.metadata = new HashMap<>();
            String metadataContent = "";
            state.currentJsonFile = fileName;
            state.currentJsonFilePath = null;
            state.currentMetadataFile = metadataFileName;
            state.currentMetadataFilePath = null;

            try {
                ServerInteraction.sendPostRequest("/create-file", state.currentJsonFile, state.json, state.currentMetadataFile, metadataContent);
                this.stateManager.update();
                JOptionPane.showMessageDialog(null, "Файл створено на сервері", "Файл створено", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateMetadataFileName(String jsonFileName) {
        return "metadata_" + jsonFileName;
    }

    @Override
    public void open(String filePath) {
        try {
            List<String> fileList = ServerInteraction.getFileList();
            String selectedFile = showFileListDialog(fileList);
            if (selectedFile != null) {
                Map<String, String> fileContents = ServerInteraction.getFileContent(selectedFile);
                String meta = fileContents.get("metadataContent");

                State state = this.stateManager.getState();
                state.metadata = new HashMap<>();
                state.currentJsonFile = selectedFile;
                state.currentMetadataFile = "metadata_" + state.currentJsonFile.replaceFirst("\\.json$", "") + ".txt";
                state.json = fileContents.get("jsonContent");
                this.metadataFileManager.loadMetadataFromFile(meta);
                state.currentJsonFilePath = null;
                state.currentMetadataFilePath = null;

                stateManager.update();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String showFileListDialog(List<String> fileList) {
        if (fileList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Список файлів порожній", "Помилка", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String[] options = fileList.toArray(new String[0]);
        return (String) JOptionPane.showInputDialog(
                null,
                "Оберіть файл для відкривання:",
                "Відкривання файлу",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    @Override
    public void save() {
        State state = this.stateManager.getState();

        try {
            String metadataContent = "[";
            for (Map.Entry<String, String> entry : stateManager.getState().metadata.entrySet()) {
                metadataContent += entry.getKey() + "=" + entry.getValue() + ", ";
            }
            if (!stateManager.getState().metadata.isEmpty()) {
                metadataContent = metadataContent.substring(0, metadataContent.length() - 2);
            }
            metadataContent += "]";
            ServerInteraction.sendPostRequest("/save-file", state.currentJsonFile, state.json, state.currentMetadataFile, metadataContent);
            this.stateManager.update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
    }
}
