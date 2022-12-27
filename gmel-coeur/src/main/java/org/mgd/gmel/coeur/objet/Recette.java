package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.dto.RecetteDto;
import org.mgd.gmel.coeur.persistence.ProduitQuantifierJao;
import org.mgd.gmel.coeur.persistence.RecetteJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.jab.objet.JocTreeSet;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.SortedSet;

/**
 * Objet métier représentant une recette de cuisine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Recette extends Jo<RecetteDto> implements Comparable<Recette> {
    private final Joc<String> nom = new Joc<>(this);
    private final Joc<Integer> nombrePersonnes = new Joc<>(this);
    private final SortedSet<ProduitQuantifier> produitsQuantifier = new JocTreeSet<>(this);

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public Integer getNombrePersonnes() {
        return nombrePersonnes.get();
    }

    public void setNombrePersonnes(Integer nombrePersonnes) {
        this.nombrePersonnes.set(nombrePersonnes);
    }

    public void setNombrePersonnes(Number nombrePersonnes) {
        this.nombrePersonnes.set(nombrePersonnes.intValue());
    }

    public SortedSet<ProduitQuantifier> getProduitsQuantifier() {
        return produitsQuantifier;
    }

    @Override
    public RecetteDto dto() {
        return new RecetteJao().decharger(this);
    }

    @Override
    public void depuis(RecetteDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonVide(dto.getNom(), "Le nom de recette \"{0}\" est incorrect");
        Verifications.nonNull(dto.getProduitsQuantifier(), "Les produits quantifiés d''une recette devrait être une liste éventuellement vide");
        Verifications.nonNegatif(dto.getNombrePersonnes(), "Le nombre de personne d''une recette doit être un entier strictement positif");

        setNom(dto.getNom());
        setNombrePersonnes(dto.getNombrePersonnes());
        getProduitsQuantifier().addAll(new ProduitQuantifierJao().charger(dto.getProduitsQuantifier(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Recette recette)) return false;
        return nom.idem(recette.nom)
                && nombrePersonnes.idem(recette.nombrePersonnes)
                && ((JocTreeSet<ProduitQuantifier>) produitsQuantifier).idem(recette.produitsQuantifier);
    }

    @Override
    public int compareTo(Recette recette) {
        return nom.get().compareToIgnoreCase(recette.nom.get());
    }

    @Override
    public boolean equals(Object objet) {
        return super.equals(objet);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
