package texteditor.model;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GUIFileOperations {
    private static final String DEFAULT_FILE_NAME = "New Document.txt";
    private final Stage stage;

    public GUIFileOperations(Stage stage) {
        this.stage = stage;
    }

    public File showOpenDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Document");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showOpenDialog(stage);
    }

    public File showSaveDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Document");
        fileChooser.setInitialFileName(DEFAULT_FILE_NAME);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showSaveDialog(stage);
    }

    public String readFile(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    public void saveFile(File file, String content) throws IOException {
        Path path = file.getAbsoluteFile().toPath();
        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}