package main.body.exporter;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextExport implements IExportStrategy {
    public void exportJson(String json) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath() + ".txt";
                FileWriter writer = new FileWriter(filePath);

                json = json.replaceAll("\\s", "");

                writer.write(json);
                writer.close();
                JOptionPane.showMessageDialog(null, "Текст успішно експортовано");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Помилка експортування тексту: " + e.getMessage());
            }
        }
    }
}
