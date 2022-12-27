package org.mgd.gmel.javafx.controle;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import org.mgd.gmel.coeur.objet.Formule;
import org.mgd.gmel.coeur.objet.LivreCuisine;
import org.mgd.gmel.coeur.objet.Recette;
import org.mgd.gmel.javafx.controle.evenement.BoutonIconeEvent;
import org.mgd.gmel.javafx.service.BibliothequeService;
import org.mgd.gmel.javafx.service.MenuService;

import java.text.MessageFormat;
import java.util.Objects;

@SuppressWarnings("java:S110")
public class BoutonFormule extends Bouton {
    private static final String DEFAULT_STYLE_CLASS = "bouton-formule";
    private static final PseudoClass PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    public static final String PATTERN_TEXTE = "{0} pour {1} personnes sur {2} repas";

    private final MenuService menuService = MenuService.getInstance();
    private final BibliothequeService bibliothequeService = BibliothequeService.getInstance();
    private final StringProperty texte;
    private final ObjectProperty<Formule> formule;
    private final ObjectProperty<Background> fond;
    private final ObjectProperty<Boolean> avertissement;
    private final BooleanBinding formuleSelectionnee;

    public BoutonFormule(Formule intiale) {
        this.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        this.texte = new SimpleStringProperty(this, "texte");
        this.formule = new SimpleObjectProperty<>(this, "formule");
        this.fond = new SimpleObjectProperty<>(this, "fond");
        this.avertissement = new SimpleObjectProperty<>(this, "avertissement");

        this.formule.addListener((observable, ancienne, nouvelle) -> {
            majTexte();
            majAvertissement();
        });
        this.formule.set(intiale);

        this.formuleSelectionnee = this.formule.isEqualTo(menuService.formuleProperty());
        this.formuleSelectionnee.subscribe(egal -> this.pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, egal));

        bibliothequeService.livresCuisineProperty().addListener((ListChangeListener<LivreCuisine>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    this.avertissement.set(change.getRemoved().stream().anyMatch(livreCuisine -> livreCuisine.getRecettes().stream().anyMatch(formule.get().getRecette()::equals)));
                }
            }
        });
        bibliothequeService.recettesProperty().addListener((ListChangeListener<Recette>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    this.avertissement.set(change.getRemoved().stream().anyMatch(formule.get().getRecette()::equals));
                }
            }
        });
        bibliothequeService.recetteNomProperty().addListener((observable, ancien, nouveau) -> majTexte());
        menuService.formuleRecetteProperty().addListener((observable, ancienne, nouvelle) -> {
            majTexte();
            majAvertissement();
        });
        menuService.formuleNombreConvivesProperty().addListener((observable, ancien, nouveau) -> majTexte());

        addEventHandler(BoutonIconeEvent.RELACHER, evenement -> {
            if (Objects.requireNonNull(evenement.getType()) == BoutonIconeType.SUPPRIMER) {
                menuService.formulesProperty().remove(formule.get());
            }
        });
    }

    private void majAvertissement() {
        this.avertissement.set(formule.get().enVigueur(bibliothequeService.bibliothequeProperty().get()));
    }

    private void majTexte() {
        this.texte.set(MessageFormat.format(PATTERN_TEXTE, formule.get().getRecette().getNom(), formule.get().getNombreConvives(), formule.get().getPeriode().getTaille()));
    }

    public StringProperty textProperty() {
        return this.texte;
    }

    public Property<Background> fondProperty() {
        return this.fond;
    }

    public ObjectProperty<Boolean> avertissementProperty() {
        return avertissement;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new BoutonFormuleTheme(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(getClass().getResource("css/bouton-formule.css")).toExternalForm();
    }

    @Override
    public void fireCliquer() {
        if (!this.isDisabled()) {
            menuService.formuleProperty().set(!formuleSelectionnee.get() ? formule.get() : null);
        }
    }
}
