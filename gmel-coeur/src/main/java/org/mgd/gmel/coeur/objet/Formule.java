package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.dto.FormuleDto;
import org.mgd.gmel.coeur.persistence.FormuleJao;
import org.mgd.gmel.coeur.persistence.PeriodeJao;
import org.mgd.gmel.coeur.persistence.RecetteJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.temps.LocalRepas;

import java.util.Comparator;

/**
 * Objet métier représentant un plat prévu sur une période.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Formule extends Jo<FormuleDto> implements Comparable<Formule> {
    private final Joc<Recette> recette = new Joc<>(this);
    private final Joc<Periode> periode = new Joc<>(this);
    private final Joc<Integer> nombreConvives = new Joc<>(this);

    public Recette getRecette() {
        return recette.get();
    }

    public void setRecette(Recette recette) {
        this.recette.set(recette);
    }

    public Periode getPeriode() {
        return periode.get();
    }

    public void setPeriode(Periode periode) {
        this.periode.set(periode);
    }

    public Integer getNombreConvives() {
        return nombreConvives.get();
    }

    public void setNombreConvives(Integer nombreConvives) {
        this.nombreConvives.set(nombreConvives);
    }

    @Override
    public FormuleDto dto() {
        return new FormuleJao().decharger(this);
    }

    @Override
    public void depuis(FormuleDto dto) throws JaoExecutionException, JaoParseException {
        if (dto.getRecette() != null) {
            setRecette(new RecetteJao().chargerParReference(dto.getRecette()));
        }

        if (dto.getPeriode() != null) {
            setPeriode(new PeriodeJao().charger(dto.getPeriode(), this));
        }

        setNombreConvives(dto.getNombreConvives());
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Formule formule)) return false;
        return recette.idem(formule.recette) && periode.idem(formule.periode);
    }

    public boolean enVigueur(LocalRepas repas) {
        long nombreDemiesJournees = getPeriode().getRepas().nombreDemiesJournees(repas);
        return 0 <= nombreDemiesJournees && nombreDemiesJournees < getPeriode().getTaille();
    }

    public boolean enVigueur(Bibliotheque bibliotheque) {
        return bibliotheque.getLivresCuisine()
                .stream()
                .noneMatch(element -> element.getRecettes().contains(recette.get()));
    }

    @Override
    public int compareTo(Formule o) {
        return Comparator.comparing(Formule::getPeriode).thenComparing(Formule::getRecette).thenComparing(Formule::getNombreConvives).compare(this, o);
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
