package main.body.validator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;
import main.state.IStateManager;
import main.state.ISubscriber;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorComponent implements IValidatorComponent, ISubscriber {
    private IStateManager stateManager;
    private IMessageDisplay messageDisplay;
    private JTextPane messagePane;
    Map<String, Object> oldJson;
    Map<String, Object> json;

    @Inject
    public ValidatorComponent(IStateManager stateManager, IMessageDisplay messageDisplay) {
        stateManager.subscribe(this);
        this.stateManager = stateManager;
        this.messageDisplay = messageDisplay;
        messagePane = new JTextPane();
        messagePane.setEditable(false);
    }

    @Override
    public JPanel getPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton validationButton = new JButton("Validation");
        validationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateMetadata();
            }
        });

        panel.add(messagePane, BorderLayout.CENTER);
        panel.add(validationButton, BorderLayout.EAST);
        return panel;
    }

    private void processJson(Map<String, Object> source) {
        json = new HashMap<>();
        processObject(source, json, "");
    }

    private static void processObject(Map<String, Object> source, Map<String, Object> destination, String prefix) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                String newPrefix = prefix.isEmpty() ? key : prefix + "." + key;
                processObject((Map<String, Object>) value, destination, newPrefix);
                destination.put(newPrefix, value);
            } else {
                String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
                destination.put(fullKey, value);
                if (value instanceof Map) {
                    processObject((Map<String, Object>) value, destination, fullKey);
                }
            }
        }
    }

    @Override
    public boolean validateMetadata() {
        boolean isValid = false;
        try {
            Map<String, String> metadata = stateManager.getState().metadata;
            String jsonString = stateManager.getState().json;
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Gson gson = new Gson();
            oldJson = gson.fromJson(jsonString, type);
            processJson(oldJson);

            String jsonSyntaxError = validateJsonSyntax(jsonString);
            for (String key : metadata.keySet()) {
                if (jsonSyntaxError == null) {
                    if (!json.containsKey(key)) {
                        this.messageDisplay.showError("Помилка валідації: Ключ '" + key + "' не знайдено в JSON.");
                    } else if (metadata.size() != json.size()) {
                        this.messageDisplay.showError("Помилка валідації: Кількість об'єктів в metadata (" + metadata.size() +
                                ") не дорівнює кількості об'єктів в JSON (" + json.size() + ").");
                    } else {
                        Object jsonValue = json.get(key);
                        String metadataType = metadata.get(key);
                        String jsonType = JsonTypeResolver.resolveType(jsonValue);

                        if (!(metadataType.trim().equals(jsonType.trim()))) {
                            this.messageDisplay.showError("Помилка валідації: Тип для ключа '" + key + "' не співпадає. " +
                                    "Очікувано: " + metadataType.trim() + ", отримано: " + jsonType.trim());
                            break;
                        } else {
                            this.messageDisplay.showSuccess("Валідація пройшла успішно.");
                            isValid = true;
                        }
                    }
                } else {
                    this.messageDisplay.showError("Помилка валідації JSON: " + jsonSyntaxError);
                }
            }
        } catch (JsonSyntaxException e) {
            int errorLine = extractErrorLine(e.getMessage());
            this.messageDisplay.showWarning("Помилка валідації JSON: помилка в рядку " + errorLine);
        }
        return isValid;
    }

    private String validateJsonSyntax(String jsonString) {
        try {
            JsonReader reader = new JsonReader(new StringReader(jsonString));
            reader.setLenient(true);
            new Gson().fromJson(reader, Object.class);
            return null;
        } catch (JsonSyntaxException e) {
            return e.getMessage();
        }
    }

    private static int extractErrorLine(String errorMessage) {
        String pattern = ".*at line (\\d+).*";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(errorMessage);

        if (m.find()) {
            return Integer.parseInt(m.group(1)) - 1;
        }
        return -1;
    }

    @Override
    public void updateMessagePane(String message, Color color) {
        StyledDocument doc = messagePane.getStyledDocument();
        Style style = messagePane.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, message, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
    }

    public static class JsonTypeResolver {
        public static String resolveType(Object value) {
            if (value instanceof String) {
                return "string";
            } else if (value instanceof Number) {
                return "number";
            } else if (value instanceof Boolean) {
                return "boolean";
            } else if (value instanceof Map) {
                return "object";
            } else {
                return "unknown";
            }
        }
    }

}
