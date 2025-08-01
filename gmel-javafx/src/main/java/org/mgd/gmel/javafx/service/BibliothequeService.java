package org.mgd.gmel.javafx.service;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.DataFormat;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.gmel.coeur.objet.Bibliotheque;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.gmel.coeur.persistence.LivreCuisineJao;
import org.mgd.gmel.coeur.persistence.RecetteJao;
import org.mgd.gmel.javafx.GmelSingletons;
import org.mgd.gmel.javafx.connexions.exception.ConnexionsException;
import org.mgd.gmel.javafx.service.exception.ServiceException;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BibliothequeService extends Service {
    public static final DataFormat FORMAT_DONNEE_RECETTE_UUID = new DataFormat("recette.uuid.custom");

    private static final BibliothequeService instance;

    static {
        try {
            instance = new BibliothequeService();
        } catch (ConnexionsException | ConnexionException | JaoExecutionException | IOException | JaoParseException e) {
            throw new ServiceException(e);
        }
    }

    private final SimpleObjectProperty<Bibliotheque> bibliotheque = new SimpleObjectProperty<>(this, "bibliothèque");
    private final SimpleListProperty<LivreCuisine> livresCuisine = new SimpleListProperty<>(this, "livres-de-cuisines");
    private final SimpleStringProperty livreCuisineNom = new SimpleStringProperty(this, "livre-de-cuisine-nom");
    private final SimpleObjectProperty<LivreCuisine> livreCuisine = new SimpleObjectProperty<>(this, "livre-de-cuisine");
    private final SimpleListProperty<Recette> recettes = new SimpleListProperty<>(this, "recettes");
    private final SimpleObjectProperty<Recette> recette = new SimpleObjectProperty<>(this, "recette");
    private final SimpleStringProperty recetteNom = new SimpleStringProperty(this, "recette-nom");
    private final SimpleIntegerProperty recetteNombrePersonnes = new SimpleIntegerProperty(this, "recette-nombre-personnes");
    private final SimpleListProperty<ProduitQuantifier> recetteProduitsQuantifier = new SimpleListProperty<>(this, "recette-produits-quantifier");
    private final SimpleObjectProperty<LivreCuisine> livreCuisineCible = new SimpleObjectProperty<>(this, "livre-de-cuisine-cible");

    private BibliothequeService() throws ConnexionsException, ConnexionException, JaoExecutionException, IOException, JaoParseException {
        livresCuisine.bind(new ListeLiaison<>(bibliotheque, Bibliotheque::getLivresCuisine));
        recettes.bind(new ListeLiaison<>(livreCuisine, LivreCuisine::getRecettes));
        recetteProduitsQuantifier.bind(new ListeLiaison<>(recette, Recette::getProduitsQuantifier));

        GmelSingletons.connexion().ouvrir();
        bibliotheque.set(GmelSingletons.connexion().getJabm().bibliotheque());

        livresCuisine.addListener(propager(bibliotheque, Bibliotheque::getLivresCuisine));
        recettes.addListener(propager(livreCuisine, LivreCuisine::getRecettes));
        recetteProduitsQuantifier.addListener(propager(recette, Recette::getProduitsQuantifier));
        livreCuisineNom.addListener((observable, ancien, nouveau) -> Optional.ofNullable(livreCuisine.get()).ifPresent(livreCuisineAModifier -> livreCuisineAModifier.setNom(nouveau)));
        recetteNom.addListener((observable, ancien, nouveau) -> Optional.ofNullable(recette.get()).ifPresent(recetteAModifier -> recetteAModifier.setNom(nouveau)));
        recetteNombrePersonnes.addListener((observable, ancien, nouveau) -> Optional.ofNullable(recette.get()).ifPresent(recetteAModifier -> recetteAModifier.setNombrePersonnes(nouveau)));
    }

    public static BibliothequeService getInstance() {
        return instance;
    }

    public void creerNouveauLivreCuisine() throws JaoExecutionException, JaoParseException {
        livresCuisine.add(new LivreCuisineJao().nouveau(nouveauLivreCuisine -> nouveauLivreCuisine.setNom("Livre de cuisine " + (livresCuisine.size() + 1))));
    }

    public LivreCuisine obtenirPremierLivreCuisineNonVide() {
        return livresCuisine.stream()
                .filter(element -> !element.getRecettes().isEmpty())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Impossible d'ajouter une formule à partir d'une bibliothèque dont tous les livres de cuisines sont sans recette."));
    }

    public LivreCuisine obtenirPremierLivreCuisineContenant(Recette recette) {
        return livresCuisine.stream()
                .filter(element -> element.getRecettes().contains(recette))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Impossible de trouver un livre de cuisine contenant la recette {0} dans la bibliothèque.", recette)));
    }

    public void creerNouvelleRecette() throws JaoExecutionException, JaoParseException {
        recettes.add(new RecetteJao().nouveau(nouvelleRecette -> {
            nouvelleRecette.setNom("Recette " + (recettes.size() + 1));
            nouvelleRecette.setNombrePersonnes(1);
        }));
    }

    public SimpleObjectProperty<Bibliotheque> bibliothequeProperty() {
        return bibliotheque;
    }

    public SimpleListProperty<LivreCuisine> livresCuisineProperty() {
        return livresCuisine;
    }

    public SimpleObjectProperty<LivreCuisine> livreCuisineProperty() {
        return livreCuisine;
    }

    public SimpleStringProperty livreCuisineNomProperty() {
        return livreCuisineNom;
    }

    public SimpleListProperty<Recette> recettesProperty() {
        return recettes;
    }

    public SimpleObjectProperty<Recette> recetteProperty() {
        return recette;
    }

    public SimpleStringProperty recetteNomProperty() {
        return recetteNom;
    }

    public SimpleIntegerProperty recetteNombrePersonnesProperty() {
        return recetteNombrePersonnes;
    }

    public SimpleListProperty<ProduitQuantifier> recetteProduitsQuantifierProperty() {
        return recetteProduitsQuantifier;
    }

    public SimpleObjectProperty<LivreCuisine> livreCuisineCibleProperty() {
        return livreCuisineCible;
    }

    public void deplacer(UUID recetteUuid, LivreCuisine livreCuisineCible) {
        Recette recetteADeplacer = recettesProperty().stream().filter(element -> element.getIdentifiant().equals(recetteUuid)).findFirst().orElseThrow(NoSuchElementException::new);
        recettesProperty().remove(recetteADeplacer);
        livreCuisineCible.getRecettes().add(recetteADeplacer);
    }
}
