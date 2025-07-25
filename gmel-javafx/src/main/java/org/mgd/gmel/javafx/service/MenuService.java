package org.mgd.gmel.javafx.service;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.gmel.coeur.Jabm;
import org.mgd.gmel.coeur.objet.*;
import org.mgd.gmel.coeur.persistence.FormuleJao;
import org.mgd.gmel.coeur.persistence.PeriodeJao;
import org.mgd.gmel.javafx.GmelSingletons;
import org.mgd.gmel.javafx.composant.MenuComposant;
import org.mgd.gmel.javafx.connexions.exception.ConnexionsException;
import org.mgd.gmel.javafx.service.exception.ServiceException;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.temps.LocalRepas;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class MenuService extends Service {
    private static final MenuService instance;
    private static final Jabm jabm;

    static {
        try {
            GmelSingletons.connexion().ouvrir();
            jabm = GmelSingletons.connexion().getJabm();
            instance = new MenuService();
        } catch (ConnexionsException | ConnexionException e) {
            throw new ServiceException(e);
        }
    }

    private final SimpleObjectProperty<LocalDate> reference = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Menu> menu = new SimpleObjectProperty<>(this, "menu");
    private final SimpleListProperty<Formule> formules = new SimpleListProperty<>(this, "formules", FXCollections.observableArrayList());
    private final SimpleObjectProperty<Formule> formule = new SimpleObjectProperty<>(this, "formule");
    private final SimpleObjectProperty<LivreCuisine> formuleLivreCuisine = new SimpleObjectProperty<>(this, "formule-livre-de-cuisine");
    private final SimpleListProperty<Recette> formuleRecettes = new SimpleListProperty<>(this, "formule-recettes", FXCollections.observableArrayList());
    private final SimpleObjectProperty<Recette> formuleRecette = new SimpleObjectProperty<>(this, "formule-recette");
    private final SimpleIntegerProperty formuleNombreConvives = new SimpleIntegerProperty(this, "formule-nombre-convives");
    private final SimpleIntegerProperty formuleTaille = new SimpleIntegerProperty(this, "formule-taille");

    private final ListChangeListener<Formule> formulesListChangeListener = getListChangeListener(() -> menu.get().getFormules());

    private MenuService() {
        reference.addListener((observable, ancienne, nouvelle) -> {
            try {
                menu.set(jabm.menu(reference.get()));
            } catch (JaoParseException | JaoExecutionException | IOException e) {
                throw new ServiceException(e);
            }
        });

        menu.addListener((observable, ancien, nouveau) -> {
            formules.removeListener(formulesListChangeListener);
            formules.clear();
            if (nouveau != null) {
                formules.setAll(nouveau.getFormules());
                formules.addListener(formulesListChangeListener);
            }
        });
        formules.addListener((ListChangeListener<Formule>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    change.getRemoved()
                            .stream()
                            .filter(formuleASupprimer -> formuleASupprimer.equals(formule.get()))
                            .findFirst()
                            .ifPresent(formuleASupprimer -> formule.set(null));
                }
                if (change.wasAdded()) {
                    change.getAddedSubList().stream().findFirst().ifPresent(formule::set);
                }
            }
        });
        formuleLivreCuisine.addListener((observable, ancien, nouveau) -> {
            if (nouveau != null) {
                formuleRecettes.setAll(nouveau.getRecettes());
            } else {
                formuleRecettes.clear();
            }
        });
        formuleRecette.addListener((observable, ancienne, nouvelle) -> Optional.ofNullable(formule.get()).ifPresent(formuleAModifier -> formuleAModifier.setRecette(nouvelle)));
        formuleNombreConvives.addListener((observable, ancien, nouveau) -> Optional.ofNullable(formule.get()).ifPresent(formuleAModifier -> formuleAModifier.setNombreConvives(nouveau.intValue())));
        formuleTaille.addListener((observable, ancienne, nouvelle) -> Optional.ofNullable(formule.get()).ifPresent(formuleAModifier -> formuleAModifier.getPeriode().setTaille(nouvelle.intValue())));
    }

    public static MenuService getInstance() {
        return instance;
    }

    public void creerNouvelleFormule(LocalRepas repas, Recette recette) throws JaoExecutionException, JaoParseException {
        formules.add(new FormuleJao().nouveau(
                (nouvelleFormule, autresJos) -> {
                    nouvelleFormule.setRecette(recette);
                    nouvelleFormule.setPeriode((Periode) autresJos[0]);
                    nouvelleFormule.setNombreConvives(MenuComposant.NOMBRE_CONVIVES_DEFAUT);
                },
                new PeriodeJao().nouveau(nouvellePeriode -> {
                    nouvellePeriode.setRepas(repas);
                    nouvellePeriode.setTaille(MenuComposant.TAILLE_DEFAUT);
                })
        ));
    }

    public void precedent() {
        reference.set(reference.get().minusDays(7));
    }

    public void aujourdhui() {
        reference.set(LocalDate.now());
    }

    public void suivant() {
        reference.set(reference.get().plusDays(7));
    }

    public SimpleObjectProperty<Menu> menuProperty() {
        return menu;
    }

    public SimpleObjectProperty<LocalDate> referenceProperty() {
        return reference;
    }

    public SimpleListProperty<Formule> formulesProperty() {
        return formules;
    }

    public SimpleObjectProperty<Formule> formuleProperty() {
        return formule;
    }

    public SimpleObjectProperty<LivreCuisine> formuleLivreCuisineProperty() {
        return formuleLivreCuisine;
    }

    public SimpleListProperty<Recette> formuleRecettesProperty() {
        return formuleRecettes;
    }

    public SimpleObjectProperty<Recette> formuleRecetteProperty() {
        return formuleRecette;
    }

    public SimpleIntegerProperty formuleNombreConvivesProperty() {
        return formuleNombreConvives;
    }

    public SimpleIntegerProperty formuleTailleProperty() {
        return formuleTaille;
    }
}
