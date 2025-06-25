package org.mgd.connexion;

import org.mgd.connexion.exception.ConnexionException;

import java.nio.file.Path;
import java.util.Objects;

public abstract class Connexion<T extends Connectable> {
    private final Path fichierConfiguration;
    private T instance;

    protected Connexion(Path fichierConfiguration) {
        this.fichierConfiguration = fichierConfiguration;
    }

    protected abstract T construire(Path chemin) throws ConnexionException;

    public void ouvrir() throws ConnexionException {
        if (!isOuverte()) {
            instance = construire(fichierConfiguration);
        }
    }

    public void fermer() {
        if (isOuverte()) {
            instance.disposer();
            instance = null;
        }
    }

    public boolean isOuverte() {
        return Objects.nonNull(instance);
    }

    public T getInstance() {
        return instance;
    }
}
