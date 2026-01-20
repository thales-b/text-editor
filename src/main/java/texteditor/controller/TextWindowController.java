package texteditor.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    private final StringProperty unsavedText = new SimpleStringProperty();

    private File currentFile = null;

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
                    unsavedText.set(textArea.getText());
                    System.out.println("Unsaved text after text area change: " + unsavedText.get());
        });

        Platform.runLater(() ->
                getStage().titleProperty().bind(Bindings.createStringBinding(() -> {
                    String filename = currentFile == null ? "New Document" : currentFile.getName();

                    return unsavedText.get().isEmpty() ?
                        filename
                        : filename + "*";
        })));
    }

    public void doSave() {
        System.out.println("Save...");
        if (currentFile == null) doSaveAs();

        writeToCurrentFile(StandardOpenOption.APPEND);
    }

    public void doSaveAs() {
        System.out.println("Save as...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("New Document.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.", "All files"),
                new FileChooser.ExtensionFilter("*.txt", "Text files")
        );
        Stage stage = getStage();
        // 'Save as' potentially changes the current file
        currentFile = fileChooser.showSaveDialog(stage);
        if (currentFile != null) {
            writeToCurrentFile(
                    // Create file if it does not exist
                    StandardOpenOption.CREATE,
                    // open it for writing
                    StandardOpenOption.WRITE,
                    // remove previous content before writing
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }
    }

    private void writeToCurrentFile(StandardOpenOption... options) {
        try {
            Path path = currentFile.getAbsoluteFile().toPath();
            Files.writeString(path, unsavedText.get(), options);

            System.out.println("Saved text: \n\"" + unsavedText + "\"\n To file '" + currentFile.getName() + "'");
            unsavedText.set("");
            System.out.println("Unsaved text after writing: " + unsavedText.get());
        } catch (IOException ex) {
            System.out.println("Failed to write in file " + currentFile.getName() + "\nCause: " + ex.getMessage());
        }
    }

    private Stage getStage() {
        Scene scene = root.getScene();
        System.out.println(scene);
        return (Stage) scene.getWindow();
    }
}
