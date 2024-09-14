package main.menu;

public interface IMetadataFileManager extends IFileManager
{
    void loadMetadataFromFile(String metadataFileContent);
}
