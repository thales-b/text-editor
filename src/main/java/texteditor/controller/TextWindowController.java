package texteditor.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class TextWindowController {
    @FXML
    private TabPane root;

    public void addTab() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/texteditor/tab.fxml"));
            Tab newTab = loader.load();

            int plusIndex = root.getTabs().size() - 1;
            root.getTabs().add(plusIndex, newTab);
            root.getSelectionModel().select(newTab);

        } catch (IOException e) {
            System.out.println("Couldn't add tab: " + e.getCause());
        }
    }
}