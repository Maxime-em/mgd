package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.dto.MenuDto;
import org.mgd.gmel.coeur.persistence.FormuleJao;
import org.mgd.gmel.coeur.persistence.MenuJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.Joc;
import org.mgd.jab.objet.JocTreeSet;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.Comparator;
import java.util.SortedSet;

/**
 * Objet métier représentant le menu de la semaine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Menu extends Jo<MenuDto> implements Comparable<Menu> {
    private final Joc<Integer> annee = new Joc<>(this);
    private final Joc<Integer> semaine = new Joc<>(this);
    private final SortedSet<Formule> formules = new JocTreeSet<>(this);

    public Integer getAnnee() {
        return annee.get();
    }

    public void setAnnee(Integer annee) {
        this.annee.set(annee);
    }

    public Integer getSemaine() {
        return semaine.get();
    }

    public void setSemaine(Integer semaine) {
        this.semaine.set(semaine);
    }

    public SortedSet<Formule> getFormules() {
        return formules;
    }

    @Override
    public MenuDto dto() {
        return new MenuJao().decharger(this);
    }

    @Override
    public void depuis(MenuDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getFormules(), "Les formules d''un menu devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getFormules(),
                (formuleDto, formuleDto2) -> formuleDto.getRecette().equals(formuleDto2.getRecette()),
                "Les recettes des formules doivent être unique"
        );
        Verifications.nonAnnee(dto.getAnnee(), "L''année doit être comprise entre {1} et {2}");
        Verifications.nonSemaine(dto.getSemaine(), dto.getAnnee(), "Le numéro de la semaine doit être compris entre {1} et {2}");

        setAnnee(dto.getAnnee());
        setSemaine(dto.getSemaine());
        getFormules().addAll(new FormuleJao().charger(dto.getFormules(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Menu menu)) return false;
        return annee.idem(menu.annee)
                && semaine.idem(menu.semaine)
                && ((JocTreeSet<Formule>) formules).idem(menu.formules);
    }

    @Override
    public int compareTo(Menu menu) {
        return Comparator.comparing(Menu::getAnnee).thenComparing(Menu::getSemaine).thenComparing(Jo::getIdentifiant).compare(this, menu);
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
