package org.mgd.gmel.javafx.composant.evenement;

import javafx.event.Event;
import javafx.event.EventType;

public class CelluleEvent<S> extends Event {
    private static final EventType<?> NOEUD_RELACHER = new EventType<>(Event.ANY, "NOEUD_RELACHER");
    private final transient S element;

    public CelluleEvent(EventType<? extends CelluleEvent<S>> eventType, S element) {
        super(eventType);
        this.element = element;
    }

    @SuppressWarnings("unchecked")
    public static <S> EventType<CelluleEvent<S>> noeudRelacherEvenementType() {
        return (EventType<CelluleEvent<S>>) NOEUD_RELACHER;
    }

    public S getElement() {
        return element;
    }
}
