package org.mgd.gmel.coeur.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.mgd.jab.JabSingletons;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.source.Ad;
import org.mgd.jab.source.Af;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.stream.Stream;

public abstract class AbstractMetierTest<D extends Dto, O extends Jo<D>, F extends Af<D, O>, A extends Ad<D, O, F>> {
    protected Path ressourcesObjets;
    protected Path ressourcesSupprimable;
    protected O attendu;
    protected A adObjet;
    protected A adSupprimable;

    protected abstract O construire();

    protected abstract A construireAd(Path ressources);

    @BeforeEach
    void setUp() throws IOException {
        JabSingletons.reinitialiser();
        JabSingletons.sauvegarde().setAsynchrone(false);

        ressourcesObjets = Paths.get("src/test/resources/base/commun");
        ressourcesSupprimable = Paths.get("src/test/resources/base/supprimable");

        try (Stream<Path> arborescence = Files.walk(ressourcesSupprimable)) {
            arborescence.filter(Files::isRegularFile).forEach(fichier -> {
                try {
                    Files.deleteIfExists(fichier);
                } catch (IOException e) {
                    System.err.println(MessageFormat.format("Impossible de supprimer le fichier \"{0}\" : {1}", fichier, e));
                }
            });
        }

        attendu = construire();
        adObjet = construireAd(ressourcesObjets);
        adSupprimable = construireAd(ressourcesSupprimable);
    }
}
