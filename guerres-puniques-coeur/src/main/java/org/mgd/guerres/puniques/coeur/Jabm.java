package org.mgd.guerres.puniques.coeur;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mgd.guerres.puniques.coeur.commun.Posture;
import org.mgd.guerres.puniques.coeur.commun.TypeArmee;
import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.guerres.puniques.coeur.objet.*;
import org.mgd.guerres.puniques.coeur.persistence.*;
import org.mgd.guerres.puniques.coeur.source.PartieAd;
import org.mgd.guerres.puniques.coeur.source.RegistreAd;
import org.mgd.jab.Jab;
import org.mgd.jab.exception.JabException;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

public class Jabm extends Jab {
    private static final Logger LOGGER = LogManager.getLogger(Jabm.class);
    private static final String NOM_PAR_DEFAUT = "defaut";

    protected Jabm(Path chemin) throws JabException {
        super(chemin);
    }

    public Registre registre() throws IOException, JaoExecutionException, JaoParseException {
        return ((RegistreAd) ads.get("registre")).registre(NOM_PAR_DEFAUT);
    }

    public Partie partie(UUID uuidFichier) throws IOException, JaoExecutionException, JaoParseException {
        return ((PartieAd) ads.get("parties")).access(uuidFichier.toString()).jo();
    }

    public void persister(String nom, Partie partie) throws IOException {
        ((PartieAd) ads.get("parties")).access(nom, partie);
    }

    public Informations creerInformations(String nom) throws JaoExecutionException, JaoParseException {
        return new InformationsJao().nouveau(nouvellesInformations -> nouvellesInformations.setNom(nom));
    }

    public Partie creerPartie(Informations informations, int nombreLignes, int nombreColonnes) throws JaoExecutionException, JaoParseException {
        Monde monde = new MondeJao().nouveau(nouveauMonde -> {
            Region[][] regions = new Region[nombreLignes][nombreColonnes];
            for (int ligne = 0; ligne < nombreLignes; ligne++) {
                for (int colonne = 0; colonne < nombreColonnes; colonne++) {
                    Region nouvelleRegion = new RegionJao().nouveau();
                    nouvelleRegion.ligne(ligne);
                    nouvelleRegion.colonne(colonne);
                    regions[ligne][colonne] = nouvelleRegion;
                }
            }
            nouveauMonde.setRegions(regions);
        });

        Des desCivilisation = new DesJao().nouveau(nouveauDes -> nouveauDes.setMaximum(6));
        Des desActions = new DesJao().nouveau(nouveauDes -> nouveauDes.setMaximum(6));

        return new PartieJao().nouveau(nouvellePartie -> {
            nouvellePartie.setInformations(informations);
            nouvellePartie.setMonde(monde);
            nouvellePartie.setDesCivilisation(desCivilisation);
            nouvellePartie.setDesActions(desActions);
        });
    }

    public Civilisation creerCivilisation(String nom, Map<TypeArmee, Integer> nombresArmees, Map<TypeUnite, Integer> nombresUnites) throws JaoExecutionException, JaoParseException {
        List<Unite> unites = nombresUnites.entrySet()
                .stream()
                .flatMap(element -> {
                    TypeUnite type = element.getKey();
                    return IntStream.range(0, element.getValue())
                            .mapToObj(index -> {
                                try {
                                    return new UniteJao().nouveau(nouvelleUnite -> {
                                        nouvelleUnite.setType(type);
                                        nouvelleUnite.setVie(type.getVie());
                                    });
                                } catch (JaoExecutionException | JaoParseException e) {
                                    LOGGER.error("Impossible de créer une nouvelle unité.", e);
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull);
                }).toList();
        Reserve reserve = new ReserveJao().nouveau(nouvelleReserve -> {
            nouvelleReserve.getUnites().addAll(unites);
            nouvelleReserve.getNombresUnitesMaximales().putAll(nombresUnites);
        });
        List<Armee> armees = nombresArmees.entrySet()
                .stream()
                .flatMap(element -> {
                    TypeArmee type = element.getKey();
                    return IntStream.range(0, element.getValue())
                            .mapToObj(index -> {
                                try {
                                    return new ArmeeJao().nouveau(nouvelleArmee -> nouvelleArmee.setType(type));
                                } catch (JaoExecutionException | JaoParseException e) {
                                    LOGGER.error("Impossible de créer une nouvelle armée.", e);
                                    return null;
                                }
                            });
                }).toList();
        Civilisation civilisation = new CivilisationJao().nouveau(nouvelleCivilisation -> {
            nouvelleCivilisation.setNom(nom);
            nouvelleCivilisation.setReserve(reserve);
            nouvelleCivilisation.getArmees().addAll(armees);
            nouvelleCivilisation.getNombresArmeesMaximales().putAll(nombresArmees);
        });
        civilisation.getArmees().forEach(armee -> {
            try {
                armee.getAlignements().add(new AlignementJao().nouveau(nouveauAlignement -> {
                    nouveauAlignement.setCivilisation(civilisation);
                    nouveauAlignement.setPosture(Posture.AMI);
                }));
            } catch (JaoExecutionException | JaoParseException e) {
                LOGGER.error("Impossible de créer un nouveau alignement.", e);
            }
        });
        return civilisation;
    }

    public Alignement creerAlignement(Civilisation civilisation, Posture posture) throws JaoExecutionException, JaoParseException {
        return new AlignementJao().nouveau(nouveauAlignement -> {
            nouveauAlignement.setCivilisation(civilisation);
            nouveauAlignement.setPosture(posture);
        });
    }
}
