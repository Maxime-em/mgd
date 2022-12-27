package org.mgd.jab.source;

import org.mgd.jab.dto.Dto;
import org.mgd.jab.objet.Jo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Les instances enfants permettent la création d'instances de type {@link Af} à partir du dossier contenant
 * les fichiers JSON. Une fois les accès aux fichiers sources instanciés, ils pourront être utilisés pour récupérer
 * les objets métiers. Si le fichier JSON n'existe pas, il doit être créé.
 *
 * @param <D> Type de l'objet de transfert {@link Dto}
 * @param <O> Type de l'objet métier {@link Jo}
 * @author Maxime
 */
public abstract class Ad<D extends Dto, O extends Jo<D>, F extends Af<D, O>> {
    private final Path dossier;
    private final SortedMap<String, F> aos = new TreeMap<>();

    protected Ad(Path dossier) {
        this.dossier = dossier;
    }

    protected abstract F access(Path source);

    public F access(String nom) throws IOException {
        return access(nom, "{}");
    }

    public F access(String nom, String contenuParDefaut) throws IOException {
        if (!aos.containsKey(nom)) {
            Path fichier = dossier.resolve(nom + ".json");
            Files.createDirectories(dossier);
            if (!Files.isRegularFile(fichier)) {
                Files.writeString(fichier, contenuParDefaut, StandardOpenOption.CREATE);
            }
            aos.put(nom, access(fichier));
        }
        return aos.get(nom);
    }
}
