package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.AgendaDto;
import org.mgd.gmel.coeur.objet.Agenda;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

/**
 * Classe permettant de manipuler les objets métiers de type {@link Agenda} et de les charger depuis un système de
 * fichiers JSON via la classe de transfert {@link AgendaDto}.
 *
 * @author Maxime
 */
public class AgendaJao extends Jao<AgendaDto, Agenda> {
    public AgendaJao() {
        super(AgendaDto.class, Agenda.class);
    }

    @Override
    public AgendaDto dto(Agenda agenda) {
        AgendaDto agendaDto = new AgendaDto();
        agendaDto.setMenus(new MenuJao().dechargerVersReferences(agenda.getMenus(), Menu.class, MenuJao.class));

        return agendaDto;
    }

    @Override
    public void enrichir(AgendaDto dto, Agenda agenda) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getMenus(), "Les menus d''un agenda devrait être une liste éventuellement vide");
        agenda.getMenus().addAll(new MenuJao().chargerParReferences(dto.getMenus()));
    }

    @Override
    protected void copier(Agenda source, Agenda cible) throws JaoExecutionException, JaoParseException {
        cible.getMenus().clear();
        cible.getMenus().addAll(new MenuJao().dupliquer(source.getMenus()));
    }
}
