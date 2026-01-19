package texteditor.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TextWindowController {
    @FXML
    private AnchorPane root;

    @FXML
    private TextArea textArea;

    private String unsavedText = "";

    @FXML
    public void initialize() {
        Platform.runLater(() ->
        {
            if (root.getScene() != null) {
                KeyCombination combo = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
                root.getScene().getAccelerators().put(combo, this::doSave);

            }
        });

        textArea.textProperty().addListener(
                (observable, oldVal, newVal) ->  {
                    unsavedText = textArea.getText();
                    System.out.println("Unsaved text after text area change: " + unsavedText);
        });
    }

    public void doSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("New Document.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.", "All files"),
                new FileChooser.ExtensionFilter("*.txt", "Text files")
        );

        Scene scene = root.getScene();
        Stage stage = (Stage) scene.getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            writeToFile(file);
        }
    }

    private void writeToFile(File file) {
        try {
            Path path = file.getAbsoluteFile().toPath();
            Files.writeString(path, unsavedText,
                    // Create file if it does not exist
                    StandardOpenOption.CREATE,
                    // open it for writing
                    StandardOpenOption.WRITE,
                    // remove previous content before writing
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println("Saved text: \n\"" + unsavedText + "\"\n To file '" + file.getName() + "'");
            unsavedText = "";
            System.out.println("Unsaved text after writing: " + unsavedText);
        } catch (IOException ex) {
            System.out.println("Failed to write in file " + file.getName() + "\nCause: " + ex.getMessage());
        }
    }
}
