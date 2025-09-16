package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.commun.Tabulable;
import org.mgd.guerres.puniques.coeur.commun.Posture;
import org.mgd.guerres.puniques.coeur.persistence.AlignementJao;
import org.mgd.jab.objet.Jo;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("java:S2160")
public class Region extends Jo implements Tabulable {
    private final Set<Alignement> alignements = new TreeSet<>();
    private final Set<TypeRegion> types = new TreeSet<>();
    private final Set<Armee> armees = new TreeSet<>();
    private final Set<Transport> transports = new TreeSet<>();
    private Integer ligne;
    private Integer colonne;

    public Set<Alignement> getAlignements() {
        return alignements;
    }

    public Set<TypeRegion> getTypes() {
        return types;
    }

    public Set<Armee> getArmees() {
        return armees;
    }

    public Set<Transport> getTransports() {
        return transports;
    }

    public void ajouterAlignementAmi(String codes, Map<String, Civilisation> civilisations) throws JaoExecutionException, JaoParseException {
        for (String code : codes.split(":")) {
            if (civilisations.containsKey(code)) {
                alignements.add(new AlignementJao().nouveau(nouveauAlignement -> {
                    nouveauAlignement.setCivilisation(civilisations.get(code));
                    nouveauAlignement.setPosture(Posture.AMI);
                }));
            }
        }
    }

    @Override
    public Integer ligne() {
        return ligne;
    }

    @Override
    public void ligne(Integer ligne) {
        this.ligne = ligne;
    }

    @Override
    public Integer colonne() {
        return colonne;
    }

    @Override
    public void colonne(Integer colonne) {
        this.colonne = colonne;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Region region)) return false;
        return ligne.equals(region.ligne) && colonne.equals(region.colonne);
    }

    public String getInformations() {
        return MessageFormat.format("Types {0}", types);
    }
}
