package org.mgd.utilitaire;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Flux {
    private Flux() {
        throw new IllegalStateException("Classe utilitaire.");
    }

    public static <T> Stream<T[]> fluxUplets(int taille, Supplier<T[]> depart, UnaryOperator<T[]> suivant) {
        AtomicReference<T[]> courant = new AtomicReference<>(depart.get());
        return Stream.concat(Stream.<T[]>of(courant.get()), IntStream.range(1, taille).mapToObj(index -> courant.updateAndGet(suivant)));
    }

    public static Stream<Integer[]> fluxPairesEntiers(int exclusif1, int exclusif2) {
        return fluxUplets(exclusif1 * exclusif2, () -> new Integer[]{0, 0, 0}, (Integer[] paire) -> paire[0] < exclusif1 - 1 ? new Integer[]{paire[0] + 1, paire[1], paire[2] + 1} : new Integer[]{0, paire[1] + 1, paire[2] + 1});
    }

    public static class CollecteurFlottants implements Collector<Float, float[], float[]> {
        private final int size;
        private int index = 0;

        public CollecteurFlottants(int size) {
            this.size = size;
        }

        @Override
        public Supplier<float[]> supplier() {
            return () -> new float[size];
        }

        @Override
        public BiConsumer<float[], Float> accumulator() {
            return (array, number) -> {
                array[index] = number;
                index++;
            };
        }

        @Override
        public BinaryOperator<float[]> combiner() {
            return null;
        }

        @Override
        public Function<float[], float[]> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }
}
