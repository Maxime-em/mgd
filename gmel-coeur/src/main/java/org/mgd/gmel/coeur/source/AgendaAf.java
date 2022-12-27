package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.AgendaDto;
import org.mgd.gmel.coeur.objet.Agenda;
import org.mgd.gmel.coeur.persistence.AgendaJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class AgendaAf extends Af<AgendaDto, Agenda> {
    protected AgendaAf(Path fichier) {
        super(new AgendaJao(), fichier);
    }
}
