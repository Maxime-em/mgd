package org.mgd.guerres.puniques.coeur;

import org.mgd.guerres.puniques.coeur.commun.Posture;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Jabm extends Jab {
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

    public Partie creerPartie(Informations informations, Collection<TypeRegion> typesRegions, int[] taille) throws JaoExecutionException, JaoParseException {
        Monde monde = new MondeJao().nouveau(nouveauMonde -> {
            nouveauMonde.getTypes().addAll(typesRegions);

            Region[][] regions = new Region[taille[0]][taille[1]];
            for (int ligne = 0; ligne < taille[0]; ligne++) {
                for (int colonne = 0; colonne < taille[1]; colonne++) {
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

    public Civilisation creerCivilisation(String nom,
                                          Collection<TypeUnite> typesUnites,
                                          Collection<TypeTransport> typesTransports,
                                          Collection<TypeArmee> typeArmees,
                                          Region capitale) throws JaoExecutionException, JaoParseException {
        List<Unite> unites = new ArrayList<>();
        for (TypeUnite type : typesUnites) {
            int maximum = type.getMaximum();
            for (int rang = 0; rang < maximum; rang++) {
                unites.add(creerUnite(type));
            }
        }

        Reserve reserve = new ReserveJao().nouveau(nouvelleReserve -> nouvelleReserve.getUnites().addAll(unites));

        return new CivilisationJao().nouveau(nouvelleCivilisation -> {
            nouvelleCivilisation.getTypesUnites().addAll(typesUnites);
            nouvelleCivilisation.getTypesTransports().addAll(typesTransports);
            nouvelleCivilisation.getTypeArmees().addAll(typeArmees);
            nouvelleCivilisation.setNom(nom);
            nouvelleCivilisation.setReserve(reserve);
            nouvelleCivilisation.setCapitale(capitale);
        });
    }

    public Unite creerUnite(TypeUnite type) throws JaoExecutionException, JaoParseException {
        return new UniteJao().nouveau(nouvelleUnite -> {
            nouvelleUnite.setType(type);
            nouvelleUnite.setVie(type.getConstitution());
        });
    }

    public Armee creerArmee(Civilisation civilisation, TypeArmee type) throws JaoExecutionException, JaoParseException {
        Armee armee = new ArmeeJao().nouveau(nouvelleArmee -> nouvelleArmee.setType(type));
        armee.getAlignements().add(new AlignementJao().nouveau(nouveauAlignement -> {
            nouveauAlignement.setCivilisation(civilisation);
            nouveauAlignement.setPosture(Posture.AMI);
        }));
        return armee;
    }

    public Transport creerTransport(TypeTransport type) throws JaoExecutionException, JaoParseException {
        return new TransportJao().nouveau(nouveauTransport -> nouveauTransport.setType(type));
    }

    public Des creerDesDegats() throws JaoExecutionException, JaoParseException {
        return new DesJao().nouveau(nouveauDes -> nouveauDes.setMaximum(6));
    }

    public TypeUnite creerTypeUnite(String nom, Collection<TypeRegion> praticables, String libelle, Integer maximum, Integer vie, Integer force) throws JaoExecutionException, JaoParseException {
        return new TypeUniteJao().nouveau(nouveauType -> {
            nouveauType.getPraticables().addAll(praticables);
            nouveauType.setNom(nom);
            nouveauType.setLibelle(libelle);
            nouveauType.setMaximum(maximum);
            nouveauType.setConstitution(vie);
            nouveauType.setForce(force);
        });
    }

    public TypeRegion creerTypeRegion(String code) throws JaoExecutionException, JaoParseException {
        return new TypeRegionJao().nouveau(nouveauType -> nouveauType.setCode(code));
    }

    public TypeTransport creerTypeTransport(String nom, Collection<TypeRegion> praticables, Integer[] textures, String libelle, Integer maximum) throws JaoExecutionException, JaoParseException {
        return new TypeTransportJao().nouveau(nouveauType -> {
            nouveauType.getPraticables().addAll(praticables);
            nouveauType.ligne(textures[0]);
            nouveauType.colonne(textures[1]);
            nouveauType.setNom(nom);
            nouveauType.setLibelle(libelle);
            nouveauType.setMaximum(maximum);
        });
    }

    public TypeArmee creerTypeArmee(String nom, Integer[] textures, String libelle, Integer maximum) throws JaoExecutionException, JaoParseException {
        return new TypeArmeeJao().nouveau(nouveauType -> {
            nouveauType.ligne(textures[0]);
            nouveauType.colonne(textures[1]);
            nouveauType.setNom(nom);
            nouveauType.setLibelle(libelle);
            nouveauType.setMaximum(maximum);
        });
    }
}
