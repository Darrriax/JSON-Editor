package main.body.exporter;

import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class TableExport implements IExportStrategy {
    public void exportJson(String json) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath() + ".csv";
                FileWriter writer = new FileWriter(filePath);

                JSONObject jsonObject = new JSONObject(json);
                processJsonObject(jsonObject, writer, "");

                writer.close();
                JOptionPane.showMessageDialog(null, "Таблицю успішно експортовано");
            } catch (IOException | JSONException e) {
                JOptionPane.showMessageDialog(null, "Помилка експортування таблиці: " + e.getMessage());
            }
        }
    }

    private void processJsonObject(JSONObject jsonObject, FileWriter writer, String indent) throws IOException, JSONException {
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                writer.append(indent + key + "______________________\n");
                processJsonObject((JSONObject) value, writer, indent + "\t");
            } else {
                writer.append(createCsvContent(key, value.toString(), indent));
            }
        }
    }

    private String createCsvContent(String key, String value, String indent) {
        return indent + key + "\t" + value + "\n";
    }
}
