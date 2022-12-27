package org.mgd.gmel.coeur;

import org.mgd.gmel.coeur.objet.*;
import org.mgd.gmel.coeur.persistence.*;
import org.mgd.gmel.coeur.source.*;
import org.mgd.jab.Jab;
import org.mgd.jab.exception.JabException;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

public class Jabm extends Jab {
    public static final String NOM_PAR_DEFAUT = "defaut";

    public Jabm(Path chemin) throws JabException {
        super(chemin);
    }

    public Bibliotheque bibliotheque() throws JaoParseException, JaoExecutionException, IOException {
        return ((BibliothequeAd) ads.get("bibliotheques")).bibliotheque(NOM_PAR_DEFAUT);
    }

    public Epicerie epicerie() throws JaoParseException, JaoExecutionException, IOException {
        return ((EpicerieAd) ads.get("epiceries")).epicerie(NOM_PAR_DEFAUT);
    }

    public Menu menu(LocalDate reference) throws JaoParseException, JaoExecutionException, IOException {
        return ((MenuAd) ads.get("menus")).menu(reference);
    }

    public Agenda agenda() throws JaoParseException, JaoExecutionException, IOException {
        return ((AgendaAd) ads.get("agendas")).agenda(NOM_PAR_DEFAUT);
    }

    public Inventaire inventaire() throws JaoParseException, JaoExecutionException, IOException {
        return ((InventaireAd) ads.get("inventaires")).inventaire("inventaire");
    }

    public BibliothequeJao bibliothequeJao() {
        return (BibliothequeJao) jaos.get("bibliotheque");
    }

    public EpicerieJao epicerieJao() {
        return (EpicerieJao) jaos.get("epicerie");
    }

    public ProduitJao produitJao() {
        return (ProduitJao) jaos.get("produit");
    }

    public ProduitQuantifierJao produitQuantifierJao() {
        return (ProduitQuantifierJao) jaos.get("produit_quantifier");
    }

    public LivreCuisineJao livreCuisineJao() {
        return (LivreCuisineJao) jaos.get("livre_cuisine");
    }

    public QuantiteJao quantiteJao() {
        return (QuantiteJao) jaos.get("quantite");
    }

    public RecetteJao recetteJao() {
        return (RecetteJao) jaos.get("recette");
    }

    public PeriodeJao periodeJao() {
        return (PeriodeJao) jaos.get("periode");
    }

    public FormuleJao formuleJao() {
        return (FormuleJao) jaos.get("formule");
    }

    public MenuJao menuJao() {
        return (MenuJao) jaos.get("menu");
    }
}
