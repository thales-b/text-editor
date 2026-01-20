package texteditor.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
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

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (root.getScene() != null) {
                KeyCombination combo = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
                root.getScene().getAccelerators().put(combo, this::doSave);
            }
        });

        textArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal != null) {
                isDirty.set(true);
            }
        });

        tab.textProperty().bind(Bindings.createStringBinding(() -> {
            String filename = (currentFile.get() == null) ? "New Document" : currentFile.get().getName();
            return isDirty.get() ? filename + " *" : filename;
        }, isDirty, currentFile));
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
        if (file != null) {
            currentFile.set(file);
            writeToCurrentFile(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private void writeToCurrentFile(StandardOpenOption... options) {
        try {
            Path path = currentFile.get().getAbsoluteFile().toPath();
            Files.writeString(path, textArea.getText(), options);
            isDirty.set(false);
        } catch (IOException ex) {
            System.out.println("Failed to write in file " + currentFile.getName() + "\nCause: " + ex.getMessage());
        }
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}