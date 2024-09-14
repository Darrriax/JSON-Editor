package main.menu.command;

import main.body.metadata.IMetadataEditor;
import main.body.metadata.AbstractMetadataPane;
import main.body.metadata.IMetadataTable;
import main.body.metadata.MetadataPanePlus;
import main.state.IStateManager;

import javax.swing.*;

public class OpenMetadataCommand implements ICommand {
    private final IStateManager stateManager;
    private final IMetadataEditor metadataEditor;
    private final IMetadataTable metadataTable;

    public OpenMetadataCommand(IStateManager stateManager, IMetadataEditor metadataEditor, IMetadataTable metadataTable) {
        this.stateManager = stateManager;
        this.metadataEditor = metadataEditor;
        this.metadataTable = metadataTable;
    }

    private void showMetadata() {
        JDialog dialog = new JDialog();
        AbstractMetadataPane metadataPane = new MetadataPanePlus(metadataEditor);
        dialog.setTitle("Metadata Editor");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(metadataEditor.setMetadataPane(metadataPane));
        dialog.setContentPane(metadataEditor.getPanel());
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void execute() {
        showMetadata();
    }
}
