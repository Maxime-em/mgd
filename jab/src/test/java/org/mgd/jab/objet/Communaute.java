package org.mgd.jab.objet;

import org.mgd.jab.dto.CommunauteDto;
import org.mgd.jab.persistence.CommunauteJao;
import org.mgd.jab.persistence.CommuneJao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.util.Set;

public class Communaute extends Jo<CommunauteDto> {
    private final Set<Commune> communes = new JocTreeSet<>(this);

    public Set<Commune> getCommunes() {
        return communes;
    }

    @Override
    public CommunauteDto dto() {
        return new CommunauteJao().decharger(this);
    }

    @Override
    public void depuis(CommunauteDto communauteDto) throws JaoExecutionException, JaoParseException {
        getCommunes().addAll(new CommuneJao().charger(communauteDto.getCommunes(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Communaute communaute)) return false;
        return ((JocTreeSet<Commune>) communes).idem(communaute.communes);
    }
}
