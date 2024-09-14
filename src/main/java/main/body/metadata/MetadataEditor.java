package main.body.metadata;

import com.google.inject.Inject;
import main.menu.IFileManager;
import main.menu.IMetadataFileManager;
import main.menu.IServerFileManager;
import main.state.IStateManager;
import main.state.ISubscriber;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MetadataEditor implements IMetadataEditor, ISubscriber {
    private IMetadataPane metadataPane;
    private final IMetadataTable metadataTable;
    private final DefaultTableModel tableModel;
    private JTable table;
    private final IStateManager stateManager;
    private final IFileManager metadataFileManager;
    private final IFileManager serverFileManager;
    private IFileManager fileManager;
    private JTextField nameField;
    private JComboBox<String> typeComboBox;

    @Inject
    public MetadataEditor(IStateManager stateManager, IMetadataFileManager metadataFileManager, IServerFileManager serverFileManager) {
        stateManager.subscribe(this);
        this.stateManager = stateManager;
        this.metadataFileManager = metadataFileManager;
        this.serverFileManager = serverFileManager;

        metadataTable = new MetadataTable(stateManager, this);
        table = metadataTable.getTable();
        tableModel = metadataTable.getTableModel();
    }

    @Override
    public JPanel setMetadataPane(IMetadataPane metadataPane) {
        this.metadataPane = metadataPane;
        nameField = metadataPane.getNameField();
        typeComboBox = metadataPane.getTypeComboBox();
        return metadataPane.getPanel();
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        if (metadataPane != null) {
            panel.add(metadataPane.getPanel(), BorderLayout.NORTH);
        }
        panel.add(metadataTable.getPanel(), BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void update() {
        if (stateManager.getState().currentJsonFilePath == null) {
            fileManager = this.serverFileManager;
        } else {
            fileManager = this.metadataFileManager;
        }
    }

    public void addMetadata(String object, String type) {
        stateManager.getState().metadata.put(object, type);
        fileManager.save();
        stateManager.update();
    }

    public void addMetadataForObject(String object, String type) {
        int selectedRow = metadataTable.getTable().getSelectedRow();

        if (selectedRow != -1) {
            String parentObject = (String) metadataTable.getTableModel().getValueAt(selectedRow, 0);
            String newKey = parentObject + "." + object;

            stateManager.getState().metadata.put(newKey, type);
            fileManager.save();
            stateManager.update();
        }
    }

    public void removeMetadata() {
        int selectedRow = metadataTable.getTable().getSelectedRow();

        if (selectedRow != -1) {
            String objectToRemove = (String) metadataTable.getTableModel().getValueAt(selectedRow, 0);
            stateManager.getState().metadata.remove(objectToRemove);

            fileManager.save();
            stateManager.update();
        }
    }

    public void editMetadata(String object, String newType) {
        int selectedRow = metadataTable.getTable().getSelectedRow();

        if (selectedRow != -1) {
            String oldObjectName = (String) metadataTable.getTableModel().getValueAt(selectedRow, 0);

            stateManager.getState().metadata.remove(oldObjectName);
            stateManager.getState().metadata.put(object, newType);

            fileManager.save();
            stateManager.update();
        }
    }

    @Override
    public IMetadataPane getMetadataPane() {
        return metadataPane;
    }
}
