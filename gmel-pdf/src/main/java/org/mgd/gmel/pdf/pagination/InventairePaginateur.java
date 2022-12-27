package org.mgd.gmel.pdf.pagination;

import org.mgd.gmel.coeur.objet.Inventaire;
import org.mgd.gmel.coeur.objet.ProduitQuantifier;
import org.mgd.gmel.pdf.pagination.producteur.ProduitQuantifierProducteur;
import org.mgd.pam.pagination.Paginateur;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InventairePaginateur extends Paginateur<Inventaire, SortedSet<ProduitQuantifier>> {
    public static final int NOMBRE_MAX_PRODUITS_QUANTIFIER_PAR_PAGE = 15;

    public InventairePaginateur() {
        super(new ProduitQuantifierProducteur());
    }

    @Override
    protected List<SortedSet<ProduitQuantifier>> lister(Inventaire inventaire) {
        AtomicInteger rang = new AtomicInteger();
        AtomicInteger nombre = new AtomicInteger();
        Map<Integer, SortedSet<ProduitQuantifier>> collect = inventaire.getProduitsQuantifier().stream().collect(Collectors.groupingBy(produitQuantifier -> {
            nombre.incrementAndGet();
            if (nombre.get() > NOMBRE_MAX_PRODUITS_QUANTIFIER_PAR_PAGE) {
                rang.getAndIncrement();
                nombre.set(0);
            }
            return rang.get();
        }, Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(produitQuantifier -> produitQuantifier.getProduit().getNom())))));
        return collect.values().stream().toList();
    }
}
