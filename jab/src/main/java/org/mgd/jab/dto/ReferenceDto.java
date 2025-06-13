package org.mgd.jab.dto;

import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.Jao;

import java.nio.file.Path;

public class ReferenceDto<D extends Dto, O extends Jo<D>, J extends Jao<D, O>> extends Dto {
    private Path chemin;
    private Class<J> classeFournisseur;

    public Path getChemin() {
        return chemin;
    }

    public void setChemin(Path chemin) {
        this.chemin = chemin;
    }

    public Class<J> getClasseFournisseur() {
        return classeFournisseur;
    }

    public void setClasseFournisseur(Class<J> classeFournisseur) {
        this.classeFournisseur = classeFournisseur;
    }
}
