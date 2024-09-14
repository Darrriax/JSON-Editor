package main.menu;

import com.google.inject.Inject;
import main.state.IStateManager;
import main.state.ISubscriber;
import main.state.State;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonFileManager implements IJsonFileManager, ISubscriber {
    private final IStateManager stateManager;

    @Inject
    public JsonFileManager(IStateManager stateManager) {
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
            State state = this.stateManager.getState();
            state.json = "";
            state.currentJsonFile = fileName;
            Path path = Paths.get(filePath.getAbsolutePath());
            state.currentJsonFilePath = path;
            try {
                Files.write(path, state.json.getBytes());

                this.stateManager.update();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void open(String filePath) {
        try {
            Path path = Paths.get(filePath);
            byte[] fileBytes = Files.readAllBytes(path);
            String fileContent = new String(fileBytes);

            State state = this.stateManager.getState();
            state.json = fileContent;
            state.currentJsonFilePath = path;
            state.currentJsonFile = path.getFileName().toString();

            this.stateManager.update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        State state = this.stateManager.getState();
        try {
            Files.write(state.currentJsonFilePath, state.json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
    }
}
