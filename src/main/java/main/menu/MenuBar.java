package main.menu;

import com.google.inject.Inject;
import main.body.history.IHistoryManager;
import main.body.metadata.IMetadataEditor;
import main.body.metadata.IMetadataTable;
import main.body.validator.IValidatorComponent;
import main.menu.command.ExportFileCommand;
import main.menu.command.ICommand;
import main.menu.command.OpenHistoryCommand;
import main.menu.command.OpenMetadataCommand;
import main.state.IStateManager;
import main.state.ISubscriber;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class MenuBar extends JMenuBar implements ISubscriber {
    private final JMenuItem fileMenu;
    private final JMenuItem historyMenu;
    private final JMenuItem exportMenu;
    private final IStateManager stateManager;
    private final IFileManager jsonFileManager;
    private final IFileManager metadataFileManager;
    private final IFileManager serverFileManager;
    private final IValidatorComponent validatorComponent;
    private IFileManager fileManager;
    private IFileManager fileManagerJson = null;
    private final IMetadataEditor metadataEditor;
    private final IMetadataTable metadataTable;
    private final IHistoryManager historyManager;
    public ICommand openMetadata;
    public ICommand openHistory;
    private JLabel fileNameLabel;

    @Inject
    public MenuBar(IValidatorComponent validatorComponent, IStateManager stateManager, IJsonFileManager jsonFileManager, IMetadataFileManager metadataFileManager, IServerFileManager serverFileManager, IMetadataEditor metadataEditor, IMetadataTable metadataTable, IHistoryManager historyManager) {
        stateManager.subscribe(this);
        this.validatorComponent = validatorComponent;
        this.stateManager = stateManager;
        this.jsonFileManager = jsonFileManager;
        this.metadataFileManager = metadataFileManager;
        this.serverFileManager = serverFileManager;
        this.metadataEditor = metadataEditor;
        this.metadataTable = metadataTable;
        this.historyManager = historyManager;
        openMetadata = new OpenMetadataCommand(stateManager, metadataEditor, metadataTable);
        openHistory = new OpenHistoryCommand(stateManager, historyManager);

        JMenu fileMenu = new JMenu("File");
        this.fileMenu = fileMenu;
        this.add(fileMenu);

        JMenu historyMenu = new JMenu("History");
        this.historyMenu = historyMenu;
        this.add(historyMenu);

        JMenuItem openMetadataItem = new JMenuItem("Metadata");
        openMetadataItem.addActionListener(e -> this.openMetadata.execute());
        this.add(openMetadataItem);

        fileNameLabel = new JLabel();
        fileNameLabel.setText("Choose file");
        this.add(fileNameLabel);

        JMenu exportMenu = new JMenu("Export");
        this.exportMenu = exportMenu;
        this.add(exportMenu);

        JMenuItem newFile = new JMenuItem("Create new...");
        this.fileMenu.add(newFile);
        newFile.addActionListener(e -> {
            this.metadataFileManager.create();
        });

        JMenuItem newServerFile = new JMenuItem("Create new on server...");
        this.fileMenu.add(newServerFile);
        newServerFile.addActionListener(e -> {
            this.serverFileManager.create();
        });

        JMenuItem openFile = new JMenuItem("Open...");
        this.fileMenu.add(openFile);
        openFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("JSON Files", "json");
            fileChooser.setFileFilter(jsonFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile.getName().endsWith(".json")) {
                    this.jsonFileManager.open(selectedFile.getAbsolutePath());
                    this.metadataFileManager.open(selectedFile.getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(null, "Будь ласка, оберіть JSON файл", "Невірний файл", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem openServerFile = new JMenuItem("Open server file...");
        this.fileMenu.add(openServerFile);
        openServerFile.addActionListener(e -> {
            this.serverFileManager.open("file");
        });

        JMenuItem saveFile = new JMenuItem("Save");
        this.fileMenu.add(saveFile);
        saveFile.addActionListener(e -> {
            if (!this.stateManager.getState().json.isEmpty()) {
                if (!this.stateManager.getState().metadata.isEmpty()) {
                    if (this.validatorComponent.validateMetadata()) {
                        this.fileManager.save();
                        if (this.fileManagerJson != null && this.stateManager.getState().currentJsonFilePath != null) {
                            this.fileManagerJson.save();
                            JOptionPane.showMessageDialog(null, "Файл успішно збережено на вашому пристрої", "Файл збережено", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(null, "Файл успішно збережено на сервері", "Файл збережено", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Невірна валідація", "Неможливо зберегти", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Метадані відсутні", "Неможливо зберегти", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "JSON файл порожній", "Неможливо зберегти", JOptionPane.ERROR_MESSAGE);
            }
        });

        JMenuItem undo = new JMenuItem("Undo");
        this.historyMenu.add(undo);
        undo.addActionListener(e -> this.stateManager.undo());

        JMenuItem savestep = new JMenuItem("Save step");
        this.historyMenu.add(savestep);
        savestep.addActionListener(e ->
        {
            this.stateManager.save();
        });

        JMenuItem openHistory = new JMenuItem("Open history");
        this.historyMenu.add(openHistory);
        openHistory.addActionListener(e ->
        {
            this.stateManager.update();
            this.openHistory.execute();
        });

        JMenuItem exportTable = new JMenuItem("To Table...");
        this.exportMenu.add(exportTable);
        exportTable.addActionListener(e ->
        {
            String json = this.stateManager.getState().json;
            ICommand exportCommand = new ExportFileCommand("table", json);
            exportCommand.execute();
        });

        JMenuItem exportText = new JMenuItem("To Text...");
        this.exportMenu.add(exportText);
        exportText.addActionListener(e ->
        {
            String json = this.stateManager.getState().json;
            ICommand exportCommand = new ExportFileCommand("text", json);
            exportCommand.execute();
        });
    }

    @Override
    public void update() {
        if (stateManager.getState().currentJsonFilePath == null) {
            fileManager = this.serverFileManager;
            fileNameLabel.setText("File on server: " + stateManager.getState().currentJsonFile);
        } else {
            fileManager = this.metadataFileManager;
            fileManagerJson = this.jsonFileManager;
            fileNameLabel.setText("File: " + stateManager.getState().currentJsonFile);
        }
    }
}
