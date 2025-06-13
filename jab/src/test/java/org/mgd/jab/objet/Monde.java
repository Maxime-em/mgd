package org.mgd.jab.objet;

import org.mgd.jab.dto.MondeDto;
import org.mgd.jab.persistence.MondeJao;
import org.mgd.jab.persistence.PaysJao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.util.Set;

public class Monde extends Jo<MondeDto> {
    private final Set<Pays> payss = new JocTreeSet<>(this);

    public Set<Pays> getPayss() {
        return payss;
    }

    @Override
    public MondeDto dto() {
        return new MondeJao().decharger(this);
    }

    @Override
    public void depuis(MondeDto mondeDto) throws JaoExecutionException, JaoParseException {
        getPayss().addAll(new PaysJao().charger(mondeDto.getPayss(), this));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Monde monde)) return false;
        return ((JocTreeSet<Pays>) payss).idem(monde.payss);
    }
}
