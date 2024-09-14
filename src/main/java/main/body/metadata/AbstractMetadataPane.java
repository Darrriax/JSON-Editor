package main.body.metadata;

import main.state.ISubscriber;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractMetadataPane implements IMetadataPane, ISubscriber {
    protected JTextField nameField;
    protected JComboBox<String> typeComboBox;
    protected IMetadataEditor metadataEditor;

    public AbstractMetadataPane(IMetadataEditor metadataEditor) {
        this.metadataEditor = metadataEditor;
    }

    @Override
    public JPanel getPanel() {
        nameField = new JTextField(6);
        typeComboBox = new JComboBox<>(new String[]{"string", "number", "boolean", "object"});

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(getRows(), 2));

        inputPanel.add(new JLabel("Object:", SwingConstants.CENTER));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Type:", SwingConstants.CENTER));
        inputPanel.add(typeComboBox);

        addButtonListeners(inputPanel);

        return inputPanel;
    }

    protected abstract int getRows();

    protected abstract void addButtonListeners(JPanel panel);

    @Override
    public void update() {
    }

    @Override
    public JTextField getNameField() {
        return nameField;
    }

    @Override
    public JComboBox<String> getTypeComboBox() {
        return typeComboBox;
    }
}
