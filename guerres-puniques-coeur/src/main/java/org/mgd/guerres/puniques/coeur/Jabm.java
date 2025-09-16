package org.mgd.guerres.puniques.coeur;

import org.mgd.guerres.puniques.coeur.commun.Alignement;
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
    public static final String NOM_PAR_DEFAUT = "defaut";

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

    public Informations creerInformations(String nom, UUID uuidFichier, Registre parent) throws JaoExecutionException, JaoParseException {
        Informations informations = new InformationsJao().nouveau(nouvellesInformations -> {
            nouvellesInformations.setNom(nom);
            nouvellesInformations.ajouterParent(parent);
        });
        parent.getInformations().put(uuidFichier, informations);
        return informations;
    }

    public Partie creerPartie(Informations informations, int nombreLignes, int nombreColonnes) throws JaoExecutionException, JaoParseException {
        Monde monde = new MondeJao().nouveau(nouveauMonde -> {
            Region[][] regions = new Region[nombreLignes][nombreColonnes];
            for (int i = 0; i < nombreLignes; i++) {
                for (int j = 0; j < nombreColonnes; j++) {
                    regions[i][j] = new RegionJao().nouveau(region -> region.setAlignement(Alignement.NEUTRE));
                }
            }
            nouveauMonde.setRegions(regions);
        });
        return new PartieJao().nouveau(nouvellePartie -> {
            nouvellePartie.setInformations(informations);
            nouvellePartie.setMonde(monde);
        });
    }

    public Civilisation creerCivilisation(String nom, Map<TypeArmee, Integer> nombresArmees, Map<TypeUnite, Integer> nombresUnites) throws JaoExecutionException, JaoParseException {
        List<Armee> armees = nombresArmees.entrySet()
                .stream()
                .flatMap(element -> {
                    TypeArmee type = element.getKey();
                    return IntStream.range(0, element.getValue())
                            .mapToObj(index -> {
                                try {
                                    return new ArmeeJao().nouveau(nouvelleArme -> {
                                        nouvelleArme.setType(type);
                                        nouvelleArme.setAlignement(Alignement.NEUTRE);
                                    });
                                } catch (JaoExecutionException | JaoParseException e) {
                                    return null;
                                }
                            });
                }).toList();
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
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull);
                }).toList();
        Reserve reserve = new ReserveJao().nouveau(nouvelleReserve -> {
            nouvelleReserve.getUnites().addAll(unites);
            nouvelleReserve.getNombresUnitesMaximales().putAll(nombresUnites);
        });
        return new CivilisationJao().nouveau(nouvelleCivilisation -> {
            nouvelleCivilisation.setNom(nom);
            nouvelleCivilisation.setReserve(reserve);
            nouvelleCivilisation.getArmees().addAll(armees);
            nouvelleCivilisation.getNombresArmeesMaximales().putAll(nombresArmees);
        });
    }
}
