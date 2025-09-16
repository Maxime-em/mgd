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
        armeeDto.setUnites(new UniteJao().decharger(armee.getUnites()));
        armeeDto.setAlignements(new AlignementJao().decharger(armee.getAlignements()));
        armeeDto.setType(armee.getType());

        return armeeDto;
    }

    @Override
    public void enrichir(ArmeeDto dto, Armee armee) throws JaoExecutionException, JaoParseException {
        armee.getUnites().addAll(new UniteJao().charger(dto.getUnites(), armee));
        armee.getAlignements().addAll(new AlignementJao().charger(dto.getAlignements(), armee));
        armee.setType(dto.getType());
    }

    @Override
    protected void copier(Armee source, Armee cible) throws JaoExecutionException, JaoParseException {
        cible.getUnites().clear();
        cible.getUnites().addAll(new UniteJao().dupliquer(source.getUnites()));
        cible.getAlignements().clear();
        cible.getAlignements().addAll(new AlignementJao().dupliquer(source.getAlignements()));
        cible.setType(source.getType());
    }
}
