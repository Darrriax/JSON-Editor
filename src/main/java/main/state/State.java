package main.state;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class State {
    public Map<String, String> metadata = new HashMap<>();
    public String json = "";
    public String currentMetadataFile = null;
    public Path currentJsonFilePath = null;
    public Path currentMetadataFilePath = null;
    public String currentJsonFile = null;
    public LocalDateTime dateTime = LocalDateTime.now();

    public State() {
    }

    // Clone constructor
    public State(State other) {
        this.json = other.json;
        this.currentMetadataFile = other.currentMetadataFile;
        this.currentJsonFile = other.currentJsonFile;
        this.currentJsonFilePath = other.currentJsonFilePath;
        this.currentMetadataFilePath = other.currentMetadataFilePath;
        this.dateTime = other.dateTime;

        // Deep clone of the Map
        this.metadata = new HashMap<>();
        for (Map.Entry<String, String> entry : other.metadata.entrySet()) {
            this.metadata.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = dateTime.format(formatter);
        return
                currentJsonFile + '\n' +
                        formattedDateTime + '\n' + '\n';
    }
}
