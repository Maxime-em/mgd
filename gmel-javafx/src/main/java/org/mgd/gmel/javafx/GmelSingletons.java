package org.mgd.gmel.javafx;

import org.mgd.gmel.javafx.connexions.Connexions;
import org.mgd.gmel.javafx.connexions.exception.ConnexionsException;

public class GmelSingletons {
    private static Connexions connexions;

    private GmelSingletons() {
    }

    public static Connexions connexion() throws ConnexionsException {
        if (connexions == null) {
            connexions = new Connexions();
        }
        return connexions;
    }
}
