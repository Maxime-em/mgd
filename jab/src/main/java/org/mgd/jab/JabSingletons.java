package org.mgd.jab;

import org.mgd.jab.dto.Dto;
import org.mgd.jab.objet.Jo;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilisée pour instancier différents singletons.
 *
 * @author Maxime
 */
public class JabSingletons {
    private static final Map<Class<? extends Jo<? extends Dto>>, JabTable<? extends Dto, ? extends Jo<? extends Dto>>> tables = new HashMap<>();
    private static JabSauvegarde sauvegarde;
    private static JabCreation creation;

    private JabSingletons() {
    }

    @SuppressWarnings("unchecked")
    public static <D extends Dto, O extends Jo<D>> JabTable<D, O> table(Class<O> classe) {
        synchronized (tables) {
            return (JabTable<D, O>) tables.computeIfAbsent(classe, aClass -> new JabTable<D, O>());
        }
    }

    public static JabSauvegarde sauvegarde() {
        if (sauvegarde == null) {
            sauvegarde = new JabSauvegarde();
        }
        return sauvegarde;
    }

    public static JabCreation creation() {
        if (creation == null) {
            creation = new JabCreation();
        }
        return creation;
    }

    public static void reinitialiser() {
        tables.clear();
        creation().reinitialiser();
        sauvegarde().reinitialiser();
    }
}
