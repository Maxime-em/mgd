package org.mgd.gmel.javafx;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.mgd.gmel.javafx.persistence.exception.ConnectionException;
import org.mgd.gmel.javafx.scene.GmelScene;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

public class GmelApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    private static void alerter(Thread t, Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        TextArea textArea = new TextArea(sw.toString());
        textArea.setWrapText(true);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Alerte");
        alert.setHeaderText(MessageFormat.format("Exception levée dans le thread {0}", t));
        alert.setResizable(true);
        alert.getDialogPane().setContent(textArea);

        alert.showAndWait();
    }

    @Override
    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(GmelApplication::alerter);
    }

    @Override
    public void start(Stage stage) {
        try {
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
