package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.TransportDto;
import org.mgd.guerres.puniques.coeur.objet.Transport;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class TransportJao extends Jao<TransportDto, Transport> {
    public TransportJao() {
        super(TransportDto.class, Transport.class);
    }

    @Override
    public TransportDto dto(Transport transport) {
        TransportDto transportDto = new TransportDto();
        transportDto.setType(new TypeTransportJao().decharger(transport.getType()));

        return transportDto;
    }

    @Override
    public void enrichir(TransportDto dto, Transport transport) throws JaoExecutionException, JaoParseException {
        transport.setType(new TypeTransportJao().charger(dto.getType(), transport));
    }

    @Override
    protected void copier(Transport source, Transport cible) throws JaoExecutionException, JaoParseException {
        cible.setType(new TypeTransportJao().dupliquer(source.getType()));
    }
}
