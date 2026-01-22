package texteditor.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import texteditor.model.GUIFileOperations;

import java.io.File;
import java.io.IOException;

public class TextWindowController {
    @FXML
    private TabPane root;

    @FXML
    private Tab tab;

    @FXML
    private TextArea textArea;

    private final ObjectProperty<File> currentFile = new SimpleObjectProperty<>(null);
    private final StringProperty lastSavedContent = new SimpleStringProperty("");

    private GUIFileOperations fileOps;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (root.getScene() != null) {
                KeyCombination combo = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
                root.getScene().getAccelerators().put(combo, this::doSave);
                fileOps = new GUIFileOperations(getStage());
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
        performSave(currentFile.get());
    }

    public void doSaveAs() {
        if (fileOps == null) return;

        File file = fileOps.showSaveDialog();
        if (file != null) {
            performSave(file);
            currentFile.set(file);
        }
    }

    public void doOpen() {
        if (fileOps == null) return;

        File file = fileOps.showOpenDialog();
        if (file != null) {
            try {
                String content = fileOps.readFile(file);
                textArea.setText(content);
                lastSavedContent.set(content);
                currentFile.set(file);
            } catch (IOException e) {
                showError("Read Error", "Could not read the file: " + e.getMessage());
            }
        }
    }

    private void performSave(File file) {
        try {
            String content = textArea.getText();
            fileOps.saveFile(file, content);
            lastSavedContent.set(content);
        } catch (IOException e) {
            showError("Save Error", "Could not save the file: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    public void addTab() {
        ObservableList<Tab> tabs = root.getTabs();
        int lastTabIndex = tabs.size() - 1;
        Tab newTab = new Tab("New Document");
        newTab.setContent();
        tabs.add(lastTabIndex - 1, new Tab());
    }
}