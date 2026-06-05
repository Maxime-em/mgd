package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.TransportDto;
import org.mgd.guerres.puniques.coeur.objet.Partie;
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
        transportDto.setType(new TypeTransportJao().dechargerVersReference(transport.getType(), Partie.class, PartieJao.class));
        transportDto.setOrigine(new CivilisationJao().dechargerVersReference(transport.getOrigine(), Partie.class, PartieJao.class));
        transportDto.setArmees(new ArmeeJao().dechargerVersReferences(transport.getArmees(), Partie.class, PartieJao.class));

        return transportDto;
    }

    @Override
    public void enrichir(TransportDto dto, Transport transport) throws JaoExecutionException, JaoParseException {
        postChargement(transport, objet -> {
            objet.setType(new TypeTransportJao().chargerParReference(dto.getType()));
            objet.setOrigine(new CivilisationJao().chargerParReference(dto.getOrigine()));
            objet.getArmees().addAll(new ArmeeJao().chargerParReferences(dto.getArmees()));
        });
    }

    @Override
    protected void copier(Transport source, Transport cible) throws JaoExecutionException, JaoParseException {
        cible.setType(new TypeTransportJao().dupliquer(source.getType()));
        cible.setOrigine(new CivilisationJao().dupliquer(source.getOrigine()));
        cible.getArmees().clear();
        cible.getArmees().addAll(new ArmeeJao().dupliquer(source.getArmees()));
    }
}
