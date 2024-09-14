package main;

import com.google.inject.AbstractModule;
import main.body.AppBody;
import main.body.IAppBody;
import main.body.history.*;
import main.body.json.IJsonEditor;
import main.body.json.JsonEditor;
import main.body.metadata.IMetadataEditor;
import main.body.metadata.IMetadataTable;
import main.body.metadata.MetadataEditor;
import main.body.metadata.MetadataTable;
import main.body.validator.IMessageDisplay;
import main.body.validator.IValidatorComponent;
import main.body.validator.MessageDisplay;
import main.body.validator.ValidatorComponent;
import main.menu.*;
import main.state.IStateManager;
import main.state.StateManager;

import javax.swing.*;

public class ProgramModule extends AbstractModule
{
    @Override
    public void configure()
    {
        bind(IJsonToolApp.class).to(JsonToolApp.class);
        bind(IStateManager.class).to(StateManager.class);
        bind(JMenuBar.class).to(MenuBar.class);
        bind(IAppBody.class).to(AppBody.class);
        bind(IJsonEditor.class).to(JsonEditor.class);
        bind(IJsonDisplayer.class).to(JsonDisplayer.class);
        bind(IMetadataEditor.class).to(MetadataEditor.class);
        bind(IMetadataTable.class).to(MetadataTable.class);
        bind(IHistoryManager.class).to(HistoryManager.class);
        bind(IHistoryPane.class).to(HistorySidePane.class);
        bind(IValidatorComponent.class).to(ValidatorComponent.class);
        bind(IMessageDisplay.class).to(MessageDisplay.class);

        bind(IJsonFileManager.class).to(JsonFileManager.class);
        bind(IServerFileManager.class).to(ServerFileManager.class);
        bind(IMetadataFileManager.class).to(MetadataFileManager.class);
    }
}
