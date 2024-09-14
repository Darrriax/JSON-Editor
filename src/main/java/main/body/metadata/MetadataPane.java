package main.body.metadata;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MetadataPane extends AbstractMetadataPane {
    public MetadataPane(IMetadataEditor metadataEditor) {
        super(metadataEditor);
    }

    @Override
    protected int getRows() {
        return 3;
    }

    @Override
    protected void addButtonListeners(JPanel panel) {
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String object = nameField.getText();
                String type = (String) typeComboBox.getSelectedItem();
                metadataEditor.addMetadata(object, type);
            }
        });
        panel.add(addButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metadataEditor.removeMetadata();
            }
        });
        panel.add(deleteButton);
    }
}
