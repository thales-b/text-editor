module texteditor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens texteditor to javafx.fxml;
    opens texteditor.controller to javafx.fxml;
    exports texteditor;
}