package org.mgd.gmel.javafx.service;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.gmel.coeur.commun.Mesure;
import org.mgd.gmel.coeur.objet.Epicerie;
import org.mgd.gmel.coeur.objet.Produit;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.coeur.objet.Quantite;
import org.mgd.gmel.coeur.persistence.ProduitJao;
import org.mgd.gmel.coeur.persistence.ProduitQuantifierJao;
import org.mgd.gmel.coeur.persistence.QuantiteJao;
import org.mgd.gmel.javafx.GmelSingletons;
import org.mgd.gmel.javafx.composant.exception.ComposantException;
import org.mgd.gmel.javafx.connexions.exception.ConnexionsException;
import org.mgd.gmel.javafx.service.exception.ServiceException;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.util.Optional;

public class EpicerieService extends Service {
    private static final EpicerieService instance;

    static {
        try {
            instance = new EpicerieService();
        } catch (ConnexionsException | ConnexionException | JaoExecutionException | IOException | JaoParseException e) {
            throw new ServiceException(e);
        }
    }

    private final SimpleObjectProperty<Epicerie> epicerie = new SimpleObjectProperty<>(this, "épicerie");
    private final SimpleListProperty<Produit> produits = new SimpleListProperty<>(this, "produits");
    private final SimpleObjectProperty<Produit> produit = new SimpleObjectProperty<>(this, "produit");
    private final SimpleStringProperty produitNom = new SimpleStringProperty(this, "produit-nom");

    private EpicerieService() throws ConnexionsException, ConnexionException, JaoExecutionException, IOException, JaoParseException {
        produits.bind(new ListeLiaison<>(epicerie, Epicerie::getProduits));

        GmelSingletons.connexion().ouvrir();
        epicerie.set(GmelSingletons.connexion().getJabm().epicerie());

        produits.addListener(propager(epicerie, Epicerie::getProduits));
        produitNom.addListener((observable, ancien, nouveau) -> Optional.ofNullable(produit.get()).ifPresent(produitAModifier -> produitAModifier.setNom(nouveau)));
    }

    public static EpicerieService getInstance() {
        return instance;
    }

    public void creerNouveauProduit() throws JaoExecutionException, JaoParseException {
        produits.add(new ProduitJao().nouveau(nouveauProduit -> nouveauProduit.setNom("Produit " + (produits.size() + 1))));
    }

    public ProduitQuantifier creerNouveauProduitQuantifier() throws JaoExecutionException, JaoParseException {
        return new ProduitQuantifierJao().nouveau((nouveauProduitQuantifier, autresJos) -> {
                    nouveauProduitQuantifier.setProduit((Produit) autresJos[0]);
                    nouveauProduitQuantifier.setQuantite((Quantite) autresJos[1]);
                },
                produits.stream()
                        .findFirst()
                        .orElseThrow(() -> new ComposantException("Impossible d'ajouter un produit depuis une épicerie vide.")),
                new QuantiteJao().nouveau(nouvelleQuantite -> {
                            nouvelleQuantite.setValeur(0L);
                            nouvelleQuantite.setMesure(Mesure.MASSE);
                        }
                )

        );
    }

    public ProduitQuantifier creerNouveauProduitQuantifier(Produit produit, Long valeur, Mesure mesure) throws JaoExecutionException, JaoParseException {
        return new ProduitQuantifierJao().nouveau((nouveauProduitQuantifier, autresJos) -> {
                    nouveauProduitQuantifier.setProduit(produit);
                    nouveauProduitQuantifier.setQuantite((Quantite) autresJos[0]);
                },
                new QuantiteJao().nouveau(nouvelleQuantite -> {
                    nouvelleQuantite.setValeur(valeur);
                    nouvelleQuantite.setMesure(mesure);
                }));
    }

    public SimpleListProperty<Produit> produitsProperty() {
        return produits;
    }

    public SimpleObjectProperty<Produit> produitProperty() {
        return produit;
    }

    public SimpleStringProperty produitNomProperty() {
        return produitNom;
    }
}
