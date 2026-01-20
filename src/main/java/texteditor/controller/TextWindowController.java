package texteditor.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TextWindowController {
    @FXML
    private TabPane root;

    @FXML
    private Tab tab;

    @FXML
    private TextArea textArea;

    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
    private final ObjectProperty<File> currentFile = new SimpleObjectProperty<>(null);
    private final StringProperty lastSavedContent = new SimpleStringProperty("");

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (root.getScene() != null) {
                KeyCombination combo = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
                root.getScene().getAccelerators().put(combo, this::doSave);
            }
        });

        tab.textProperty().bind(Bindings.createStringBinding(() -> {
            String filename = (currentFile.get() == null) ? "New Document" : currentFile.get().getName();

            String current = textArea.getText();
            String saved = lastSavedContent.get();

            boolean isModified = !current.equals(saved);

            return isModified ? filename + "*" : filename;
        }, textArea.textProperty(), lastSavedContent, currentFile));
    }

    public void doSave() {
        if (currentFile.get() == null) {
            doSaveAs();
            return;
        }
        writeToCurrentFile(StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public void doSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("New Document.txt");
        File file = fileChooser.showSaveDialog(getStage());
        if (file == null) {
            System.out.println("Could not save file");
            return;
        }
        currentFile.set(file);
        writeToCurrentFile(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void doOpen() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(getStage());
        if (file == null) {
            System.out.println("Could not open file");
            return;
        }
        currentFile.set(file);
    }

    private void writeToCurrentFile(StandardOpenOption... options) {
        try {
            Path path = currentFile.get().getAbsoluteFile().toPath();
            Files.writeString(path, textArea.getText(), options);
            lastSavedContent.set(textArea.getText());
        } catch (IOException ex) {
            System.out.println("Failed to write in file " + currentFile.getName() + "\nCause: " + ex.getMessage());
        }
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}