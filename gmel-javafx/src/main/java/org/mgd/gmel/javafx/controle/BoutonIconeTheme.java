package org.mgd.gmel.javafx.controle;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Region;

public class BoutonIconeTheme extends SkinBase<BoutonIcone> {
    private final BehaviorBase<BoutonIcone> comportement;


    public BoutonIconeTheme(BoutonIcone boutonIcone) {
        super(boutonIcone);
        this.comportement = new BoutonComportement<>(boutonIcone);

        Region region = new Region();
        region.getStyleClass().setAll("icone");

        this.getChildren().setAll(region);
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
