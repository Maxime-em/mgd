package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.AgendaDto;
import org.mgd.gmel.coeur.objet.Agenda;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;

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
    protected AgendaDto to(Agenda agenda) {
        AgendaDto agendaDto = new AgendaDto();
        agendaDto.setMenus(new MenuJao().decharger(agenda.getMenus()));

        return agendaDto;
    }

    @Override
    protected void copier(Agenda source, Agenda cible) throws JaoExecutionException {
        source.getMenus().clear();
        source.getMenus().addAll(new MenuJao().dupliquer(cible.getMenus()));
    }
}
