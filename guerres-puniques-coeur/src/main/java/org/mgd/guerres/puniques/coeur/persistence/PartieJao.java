package org.mgd.guerres.puniques.coeur.persistence;

import org.mgd.guerres.puniques.coeur.dto.AlignementDto;
import org.mgd.guerres.puniques.coeur.dto.PartieDto;
import org.mgd.guerres.puniques.coeur.objet.*;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.util.Optional;

public class PartieJao extends Jao<PartieDto, Partie> {
    public PartieJao() {
        super(PartieDto.class, Partie.class);
    }

    @Override
    public PartieDto dto(Partie partie) {
        PartieDto partieDto = new PartieDto();
        partieDto.setInformations(new InformationsJao().dechargerVersReference(partie.getInformations(), Registre.class, RegistreJao.class));
        partieDto.setMonde(new MondeJao().decharger(partie.getMonde()));
        partieDto.setCivilisations(new CivilisationJao().decharger(partie.getCivilisations()));
        partieDto.setDesCivilisation(new DesJao().decharger(partie.getDesCivilisation()));
        partieDto.setDesActions(new DesJao().decharger(partie.getDesActions()));

        return partieDto;
    }

    @Override
    public void enrichir(PartieDto dto, Partie partie) throws JaoExecutionException, JaoParseException {
        partie.getCivilisations().addAll(new CivilisationJao().charger(dto.getCivilisations(), partie));
        partie.setInformations(new InformationsJao().chargerParReference(dto.getInformations()));
        partie.setMonde(new MondeJao().charger(dto.getMonde(), partie));
        partie.setDesCivilisation(new DesJao().charger(dto.getDesCivilisation(), partie));
        partie.setDesActions(new DesJao().charger(dto.getDesActions(), partie));
    }

    @Override
    public void enrichirPostCreation(PartieDto dto, Partie partie) throws JaoExecutionException, JaoParseException {
        for (Civilisation civilisation : partie.getCivilisations()) {
            for (Armee armee : civilisation.getArmees()) {
                for (Alignement alignement : armee.getAlignements()) {
                    Optional<AlignementDto> dtoOptional = dto.getCivilisations()
                            .stream()
                            .filter(civilisationDto -> civilisationDto.getIdentifiant().equals(civilisation.getIdentifiant().toString()))
                            .findFirst()
                            .orElseThrow()
                            .getArmees()
                            .stream()
                            .filter(armeeDto -> armeeDto.getIdentifiant().equals(armee.getIdentifiant().toString()))
                            .findFirst()
                            .orElseThrow()
                            .getAlignements()
                            .stream()
                            .filter(alignementDto -> alignementDto.getIdentifiant().equals(alignement.getIdentifiant().toString()))
                            .findFirst();
                    if (dtoOptional.isPresent()) {
                        alignement.setCivilisation(new CivilisationJao().chargerParReference(dtoOptional.get().getCivilisation()));
                    }
                }
            }
        }

        for (Region region : partie.getMonde().fluxRegions().toList()) {
            for (Alignement alignement : region.getAlignements()) {
                Optional<AlignementDto> dtoOptional = dto.getMonde()
                        .fluxRegions()
                        .filter(regionDto -> regionDto.getIdentifiant().equals(region.getIdentifiant().toString()))
                        .findFirst()
                        .orElseThrow()
                        .getAlignements()
                        .stream()
                        .filter(alignementDto -> alignementDto.getIdentifiant().equals(alignement.getIdentifiant().toString()))
                        .findFirst();
                if (dtoOptional.isPresent()) {
                    alignement.setCivilisation(new CivilisationJao().chargerParReference(dtoOptional.get().getCivilisation()));
                }
            }
        }
    }

    @Override
    protected void copier(Partie source, Partie cible) throws JaoExecutionException, JaoParseException {
        cible.getCivilisations().clear();
        cible.getCivilisations().addAll(new CivilisationJao().dupliquer(source.getCivilisations()));
        cible.setInformations(new InformationsJao().dupliquer(source.getInformations()));
        cible.setMonde(new MondeJao().dupliquer(source.getMonde()));
        cible.setDesCivilisation((new DesJao().dupliquer(source.getDesCivilisation())));
        cible.setDesActions(new DesJao().dupliquer(source.getDesActions()));
    }
}
