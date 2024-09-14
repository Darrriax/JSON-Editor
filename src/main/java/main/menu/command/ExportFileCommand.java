package main.menu.command;

import main.body.exporter.IExportStrategy;
import main.body.exporter.TableExport;
import main.body.exporter.TextExport;

import javax.swing.*;

public class ExportFileCommand implements ICommand {
    private final String exportType;
    private final String json;

    public ExportFileCommand(String exportType, String json) {
        this.exportType = exportType;
        this.json = json;
    }

    @Override
    public void execute() {
        chooseExportOption();
    }

    private void chooseExportOption() {
        if (json.isEmpty()) {
            JOptionPane.showMessageDialog(null, "JSON порожній. Введіть JSON перед його експортуванням");
        }
        else {
            IExportStrategy strategy = switch (exportType) {
                case "table" -> new TableExport();
                case "text" -> new TextExport();
                default -> null;
            };
            if (strategy != null) {
                strategy.exportJson(json);
            }
        }
    }
}
