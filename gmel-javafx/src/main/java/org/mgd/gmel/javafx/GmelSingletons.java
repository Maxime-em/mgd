package org.mgd.gmel.javafx;

import org.mgd.gmel.javafx.persistence.Connexion;
import org.mgd.gmel.javafx.persistence.exception.ConnectionException;

public class GmelSingletons {
    private static Connexion connexion;

    private GmelSingletons() {
    }

    public static Connexion connexion() throws ConnectionException {
        if (connexion == null) {
            connexion = new Connexion();
        }
        return connexion;
    }
}
