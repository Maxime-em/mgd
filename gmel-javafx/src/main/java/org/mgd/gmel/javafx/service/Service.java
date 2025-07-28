package org.mgd.gmel.javafx.service;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.function.Function;

public abstract class Service {
    protected Service() {
    }

    public static <O, E> ListChangeListener<E> propager(ObservableObjectValue<O> source, Function<O, Collection<E>> obtenirListe) {
        return change -> {
            if (Bindings.isNotNull(source).get()) {
                Collection<E> aModifier = obtenirListe.apply(source.get());
                while (change.next()) {
                    if (change.wasRemoved()) {
                        change.getRemoved().forEach(aModifier::remove);
                    }
                    if (change.wasAdded()) {
                        aModifier.addAll(change.getAddedSubList());
                    }
                }
            }
        };
    }

    protected static class ListeLiaison<O, E> extends ListBinding<E> {
        private final ObservableObjectValue<O> source;
        private final Function<O, Collection<E>> obtenirListe;

        public ListeLiaison(ObservableObjectValue<O> source, Function<O, Collection<E>> obtenirListe) {
            this.source = source;
            this.obtenirListe = obtenirListe;
            bind(source);
        }

        @Override
        protected ObservableList<E> computeValue() {
            return Bindings.isNotNull(source).get() ? FXCollections.observableArrayList(obtenirListe.apply(source.get())) : FXCollections.emptyObservableList();
        }

        @Override
        public void dispose() {
            super.dispose();
            unbind(source);
        }
    }
}
