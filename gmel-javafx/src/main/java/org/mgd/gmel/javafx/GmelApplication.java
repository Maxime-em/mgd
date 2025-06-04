package org.mgd.gmel.javafx;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.gmel.javafx.persistence.exception.ConnectionException;
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

    private static void alerter(Thread t, Throwable e) {
        String message = MessageFormat.format("Exception levée dans le thread {0}", t);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        TextArea textArea = new TextArea(sw.toString());
        textArea.setWrapText(true);

        Alert fenetre = new Alert(Alert.AlertType.ERROR);
        fenetre.setTitle("Alerte");
        fenetre.setHeaderText(message);
        fenetre.setResizable(true);
        fenetre.getDialogPane().setContent(textArea);

        LOGGER.error(message, e);

        try {
            fenetre.showAndWait();
        } catch (IllegalStateException ex) {
            LOGGER.error("Impossible d'ouvrir une fenêtre d'alerte", ex);
        }
    }

    @Override
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(GmelApplication::alerter);
    }

    @Override
    public void start(Stage stage) {
        try {
            LOGGER.info("Démarrage de l'application");

            stage.setTitle("Gmel");
            stage.setScene(new GmelScene());
            stage.show();
        } catch (IOException e) {
            alerter(Thread.currentThread(), e);
        }
    }

    @Override
    public void stop() throws ConnectionException {
        GmelSingletons.connexion().fermer();
    }
}
