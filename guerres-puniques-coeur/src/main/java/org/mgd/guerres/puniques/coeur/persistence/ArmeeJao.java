package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.ArmeeDto;
import org.mgd.guerres.puniques.coeur.objet.Armee;
import org.mgd.guerres.puniques.coeur.objet.Partie;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

public class ArmeeJao extends Jao<ArmeeDto, Armee> {
    public ArmeeJao() {
        super(ArmeeDto.class, Armee.class);
    }

    @Override
    public ArmeeDto dto(Armee armee) {
        ArmeeDto armeeDto = new ArmeeDto();
        armeeDto.setType(new TypeArmeeJao().dechargerVersReference(armee.getType(), Partie.class, PartieJao.class));
        armeeDto.setOrigine(new CivilisationJao().dechargerVersReference(armee.getOrigine(), Partie.class, PartieJao.class));
        armeeDto.setUnites(new UniteJao().decharger(armee.getUnites()));
        armeeDto.setDesDegats(new DesJao().decharger(armee.getDesDegats()));

        return armeeDto;
    }

    @Override
    public void enrichir(ArmeeDto dto, Armee armee) throws JaoExecutionException, JaoParseException {
        armee.getUnites().addAll(new UniteJao().charger(dto.getUnites(), armee));
        armee.getDesDegats().addAll(new DesJao().charger(dto.getDesDegats(), armee));

        postChargement(armee, objet -> {
            objet.setType(new TypeArmeeJao().chargerParReference(dto.getType()));
            objet.setOrigine(new CivilisationJao().chargerParReference(dto.getOrigine()));
        });
    }

    @Override
    protected void copier(Armee source, Armee cible) throws JaoExecutionException, JaoParseException {
        cible.setType(new TypeArmeeJao().dupliquer(source.getType()));
        cible.setOrigine(new CivilisationJao().dupliquer(source.getOrigine()));
        cible.getUnites().clear();
        cible.getUnites().addAll(new UniteJao().dupliquer(source.getUnites()));
        cible.getTransports().clear();
        cible.getTransports().addAll(new TransportJao().dupliquer(source.getTransports()));
        cible.getDesDegats().clear();
        cible.getDesDegats().addAll(new DesJao().dupliquer(source.getDesDegats()));
    }
}
