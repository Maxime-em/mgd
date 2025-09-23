package org.mgd.jab.utilitaire;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;

import java.util.Arrays;
import java.util.stream.IntStream;

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

    public static boolean idem(Object[][] a, Object[][] b) {
        if (a == b) return true;
        else if (a.length != b.length) return false;
        else if (a.length == 0) return true;
        else if (a[0].length != b[0].length) return false;
        else return IntStream.range(0, a.length).allMatch(i -> Arrays.deepEquals(a[i], b[i]));
    }
}
