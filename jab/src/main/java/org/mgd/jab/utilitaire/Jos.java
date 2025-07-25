package org.mgd.jab.utilitaire;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;

public class Jos {
    private Jos() {
        throw new IllegalStateException("Classe utilitaire.");
    }

    public static boolean idem(Object a, Object b) {
        if (a == b) return true;
        else if (a instanceof Jo jo) return jo.idem(b);
        else if (a instanceof Joc<?> joc) return joc.idem(b);
        else return a.equals(b);
    }
}
