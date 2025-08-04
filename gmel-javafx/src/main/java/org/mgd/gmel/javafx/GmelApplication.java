package org.mgd.gmel.javafx;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.gmel.javafx.connexions.exception.ConnexionsException;
import org.mgd.gmel.javafx.scene.GmelScene;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

public class GmelApplication extends Application {
    private static final Logger LOGGER = LogManager.getLogger(GmelApplication.class);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        try {
            LOGGER.info("Démarrage de l'application");

            stage.setTitle("Gmel");
            stage.setScene(new GmelScene());
            stage.show();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void stop() throws ConnexionsException {
        GmelSingletons.connexion().fermer();
    }
}
