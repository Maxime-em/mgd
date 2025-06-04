package org.mgd.gmel.javafx.composant.cellule;

import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.mgd.gmel.javafx.composant.evenement.CelluleEvent;
import org.mgd.gmel.javafx.controle.BoutonIcone;
import org.mgd.gmel.javafx.controle.evenement.BoutonIconeEvent;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("java:S110")
public abstract class Cellule<S, T> extends TableCell<S, T> {
    protected final Set<BoutonIcone> boutons;
    protected final HBox barreOutils;

    protected Cellule(Node... noeuds) {
        this.boutons = Set.of(noeuds).stream().filter(BoutonIcone.class::isInstance).map(BoutonIcone.class::cast).collect(Collectors.toSet());
        this.barreOutils = noeuds.length > 0 ? new HBox(noeuds) : null;

        addEventHandler(BoutonIconeEvent.RELACHER, evenement -> {
            evenement.consume();
            switch (evenement.getType()) {
                case SUPPRIMER:
                    fireEvent(new CelluleEvent<>(CelluleEvent.noeudSupprimerEvenementType(), this.getTableRow().getItem()));
                    break;

                case AVERTIR:
                    fireEvent(new CelluleEvent<>(CelluleEvent.noeudAvertirEvenementType(), this.getTableRow().getItem()));
                    break;

                default:
                    fireEvent(new CelluleEvent<>(CelluleEvent.noeudRelacherEvenementType(), this.getTableRow().getItem()));
                    break;
            }
        });
    }
}
