package org.mgd.gmel.javafx.controle;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.scene.input.MouseEvent;

public class BoutonComportement<C extends Bouton> extends BehaviorBase<C> {
    private final InputMap<C> tableauEntrants = this.createInputMap();

    public BoutonComportement(C controle) {
        super(controle);
        this.addDefaultMapping(this.tableauEntrants, new InputMap.MouseMapping(MouseEvent.MOUSE_RELEASED, this::mouseReleased));
    }

    private void mouseReleased(MouseEvent ignoredEvenement) {
        this.getNode().fireCliquer();
    }

    @Override
    public InputMap<C> getInputMap() {
        return tableauEntrants;
    }
}
