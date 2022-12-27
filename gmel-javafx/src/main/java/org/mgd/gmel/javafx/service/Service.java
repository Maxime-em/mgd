package org.mgd.gmel.javafx.service;

import javafx.collections.ListChangeListener;

import java.util.Collection;
import java.util.function.Supplier;

public abstract class Service {
    protected Service() {
    }

    public static <T> ListChangeListener<T> getListChangeListener(Supplier<Collection<T>> liste) {
        return change -> {
            Collection<T> aModifier = liste.get();
            while (change.next()) {
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(aModifier::remove);
                }
                if (change.wasAdded()) {
                    aModifier.addAll(change.getAddedSubList());
                }
            }
        };
    }
}
