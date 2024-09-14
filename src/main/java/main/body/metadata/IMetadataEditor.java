package main.body.metadata;

import main.IPanelProvider;

import javax.swing.*;

public interface IMetadataEditor extends IPanelProvider
{
    JPanel setMetadataPane(IMetadataPane metadataPane);

    void addMetadata(String object, String type);

    void addMetadataForObject(String object, String type);

    void removeMetadata();

    void editMetadata(String object, String newType);

    IMetadataPane getMetadataPane();
}
