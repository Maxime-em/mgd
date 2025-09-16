package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.ArmeeDto;
import org.mgd.guerres.puniques.coeur.objet.Armee;
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
        armeeDto.setAlignement(armee.getAlignement());
        armeeDto.setUnites(new UniteJao().decharger(armee.getUnites()));

        return armeeDto;
    }

    @Override
    public void enrichir(ArmeeDto dto, Armee armee) throws JaoExecutionException, JaoParseException {
        armee.setAlignement(dto.getAlignement());
        armee.getUnites().addAll(new UniteJao().charger(dto.getUnites(), armee));
    }

    @Override
    protected void copier(Armee source, Armee cible) throws JaoExecutionException, JaoParseException {
        cible.setAlignement(source.getAlignement());
        cible.getUnites().clear();
        cible.getUnites().addAll(new UniteJao().dupliquer(source.getUnites()));
    }
}
