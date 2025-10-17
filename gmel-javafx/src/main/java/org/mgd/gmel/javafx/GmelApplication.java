package org.mgd.gmel.javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.gmel.javafx.connexions.exception.ConnexionsException;
import org.mgd.gmel.javafx.scene.GmelScene;

public class GmelApplication extends Application {
    private static final Logger LOGGER = LogManager.getLogger(GmelApplication.class);

    static void main() {
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
