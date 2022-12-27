package org.mgd.gmel.coeur.objet;

import org.mgd.gmel.coeur.dto.AgendaDto;
import org.mgd.gmel.coeur.persistence.AgendaJao;
import org.mgd.gmel.coeur.persistence.MenuJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.objet.JocTreeSet;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.util.SortedSet;

/**
 * Objet métier représentant un agenda des menus de la semaine.
 *
 * @author Maxime
 */
@SuppressWarnings("java:S2160")
public class Agenda extends Jo<AgendaDto> {
    private final SortedSet<Menu> menus = new JocTreeSet<>(this);

    public SortedSet<Menu> getMenus() {
        return menus;
    }

    @Override
    public AgendaDto dto() {
        return new AgendaJao().decharger(this);
    }

    @Override
    public void depuis(AgendaDto dto) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getMenus(), "Les menus d''un agenda devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getMenus(),
                (menuDto1, menuDto2) -> menuDto1.getAnnee().equals(menuDto2.getAnnee()) && menuDto1.getSemaine().equals(menuDto2.getSemaine()),
                "Les semaines annuelles des menus dans un agenda doivent être unique"
        );
        getMenus().addAll(new MenuJao().charger(dto.getMenus(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Agenda agenda)) return false;
        return ((JocTreeSet<Menu>) menus).idem(agenda.menus);
    }
}
