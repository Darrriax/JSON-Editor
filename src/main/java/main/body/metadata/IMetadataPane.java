package main.body.metadata;

import main.IPanelProvider;

import javax.swing.*;

public interface IMetadataPane extends IPanelProvider
{
    JTextField getNameField();
    JComboBox<String> getTypeComboBox();
}
