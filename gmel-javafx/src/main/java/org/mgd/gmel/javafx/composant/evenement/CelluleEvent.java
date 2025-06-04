package org.mgd.gmel.javafx.composant.evenement;

import javafx.event.Event;
import javafx.event.EventType;

public class CelluleEvent<S> extends Event {
    private static final EventType<?> NOEUD_RELACHER = new EventType<>(Event.ANY, "NOEUD_RELACHER");
    private static final EventType<?> NOEUD_SUPPRIMER = new EventType<>(Event.ANY, "NOEUD_SUPPRIMER");
    private static final EventType<?> NOEUD_AVERTIR = new EventType<>(Event.ANY, "NOEUD_AVERTIR");

    private final transient S element;

    public CelluleEvent(EventType<? extends CelluleEvent<S>> eventType, S element) {
        super(eventType);
        this.element = element;
    }

    @SuppressWarnings("unchecked")
    public static <S> EventType<CelluleEvent<S>> noeudRelacherEvenementType() {
        return (EventType<CelluleEvent<S>>) NOEUD_RELACHER;
    }

    @SuppressWarnings("unchecked")
    public static <S> EventType<CelluleEvent<S>> noeudSupprimerEvenementType() {
        return (EventType<CelluleEvent<S>>) NOEUD_SUPPRIMER;
    }

    @SuppressWarnings("unchecked")
    public static <S> EventType<CelluleEvent<S>> noeudAvertirEvenementType() {
        return (EventType<CelluleEvent<S>>) NOEUD_AVERTIR;
    }

    public S getElement() {
        return element;
    }
}
