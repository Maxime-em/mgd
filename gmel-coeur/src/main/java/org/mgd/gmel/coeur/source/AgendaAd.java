package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.AgendaDto;
import org.mgd.gmel.coeur.objet.Agenda;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;

public class AgendaAd extends Ad<AgendaDto, Agenda, AgendaAf> {
    public AgendaAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected AgendaAf access(Path source) {
        return new AgendaAf(source);
    }

    public Agenda agenda(String nom) throws IOException, JaoExecutionException, JaoParseException {
        return access(nom, "{\"menus\":[]}").jo();
    }
}
