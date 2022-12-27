package org.mgd.gmel.javafx.controle.evenement;

import javafx.event.Event;
import javafx.event.EventType;
import org.mgd.gmel.javafx.controle.BoutonIconeType;

public class BoutonIconeEvent extends Event {
    public static final EventType<BoutonIconeEvent> RELACHER = new EventType<>(Event.ANY, "RELACHER");
    private final BoutonIconeType type;

    public BoutonIconeEvent(BoutonIconeType type) {
        super(RELACHER);
        this.type = type;
    }

    public BoutonIconeType getType() {
        return type;
    }
}
