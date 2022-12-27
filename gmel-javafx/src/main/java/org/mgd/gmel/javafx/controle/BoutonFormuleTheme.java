package org.mgd.gmel.javafx.controle;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class BoutonFormuleTheme extends SkinBase<BoutonFormule> {
    private final BehaviorBase<BoutonFormule> comportement;
    private final BoutonIcone supprimer;
    private final BoutonIcone avertir;
    private final Text texte;
    private final HBox volet;

    protected BoutonFormuleTheme(BoutonFormule boutonFormule) {
        super(boutonFormule);

        this.comportement = new BoutonComportement<>(boutonFormule);

        supprimer = new BoutonIcone(BoutonIconeType.SUPPRIMER, BoutonIconeTaille.NORMALE);
        avertir = new BoutonIcone(BoutonIconeType.AVERTIR, BoutonIconeTaille.NORMALE);

        texte = new Text();
        texte.textProperty().bind(boutonFormule.textProperty());
        texte.getStyleClass().setAll("texte");

        volet = new HBox();
        volet.backgroundProperty().bind(boutonFormule.fondProperty());
        volet.getStyleClass().setAll("volet");
        remplirVolet(boutonFormule.avertissementProperty().get());

        boutonFormule.avertissementProperty().addListener((observable, ancien, nouveau) -> remplirVolet(nouveau));

        this.getChildren().setAll(volet);
    }

    private void remplirVolet(Boolean avertissement) {
        if (Boolean.TRUE.equals(avertissement)) {
            volet.getChildren().setAll(avertir, supprimer, texte);
        } else {
            volet.getChildren().setAll(supprimer, texte);
        }
    }

    @Override
    public void dispose() {
        if (this.getSkinnable() != null) {
            super.dispose();
            if (this.comportement != null) {
                this.comportement.dispose();
            }
        }
    }
}
