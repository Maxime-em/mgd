package org.mgd.gmel.javafx.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Objects;

public class GmelScene extends Scene {
    public GmelScene() throws IOException {
        super(new Group());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/gmel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();

        getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/gmel.css")).toExternalForm());
    }
}
