package org.mgd.gmel.pdf;

import org.mgd.gmel.coeur.objet.Agenda;
import org.mgd.gmel.coeur.objet.Inventaire;
import org.mgd.gmel.pdf.pagination.AgendaPaginateur;
import org.mgd.gmel.pdf.pagination.InventairePaginateur;
import org.mgd.pam.Pab;
import org.mgd.pam.exception.PabException;

import java.nio.file.Path;

public class Pabm extends Pab {

    // TODO code similaire dans la classe Jab
    public Pabm(Path chemin) throws PabException {
        super(chemin);
    }

    public Path ecrire(Agenda agenda) throws PabException {
        return ecrire(new AgendaPaginateur(), agenda);
    }

    public Path ecrire(Inventaire inventaire) throws PabException {
        return ecrire(new InventairePaginateur(), inventaire);
    }
}
