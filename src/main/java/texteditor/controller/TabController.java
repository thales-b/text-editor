package texteditor.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import texteditor.model.GUIFileOperations;

import java.io.File;
import java.io.IOException;

public class TabController {
    @FXML
    private TabPane root;

    @FXML
    private Tab tab;

    @FXML
    private TextArea textArea;

    @FXML
    private Label statusLabel;

    private final ObjectProperty<File> currentFile = new SimpleObjectProperty<>(null);
    private final StringProperty lastSavedContent = new SimpleStringProperty("");

    private GUIFileOperations fileOps;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            root = tab.getTabPane();

            if (root != null && root.getScene() != null) {
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


        textArea.caretPositionProperty().addListener((observable, oldPos, newPos) -> {
            updateStatus(newPos.intValue());
        });
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

    public void doExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");

        ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesBtn, noBtn);
        alert.initOwner(getStage());
        alert.showAndWait();
        if (alert.getResult().getButtonData() == ButtonBar.ButtonData.YES) {
            Platform.exit();
            System.exit(0);
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

    private void updateStatus(int caretPos) {
        String text = textArea.getText();

        int line = 1;
        int col = 1;

        try {
            String textUntilCaret = text.substring(0, caretPos);

            line = textUntilCaret.length() - textUntilCaret.replace("\n", "").length() + 1;

            int lastNlIndex = textUntilCaret.lastIndexOf('\n');
            if (lastNlIndex != -1) {
                col = caretPos - lastNlIndex;
            } else {
                col = caretPos + 1;
            }

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Couldn't calculate caret pos: " + e.getCause());
        }

        statusLabel.setText("Ln " + line + ", Col " + col);
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}
