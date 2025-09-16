package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.commun.Tabulable;
import org.mgd.guerres.puniques.coeur.commun.Posture;
import org.mgd.guerres.puniques.coeur.commun.TypeRegion;
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

    public boolean estAmiAvec(Civilisation civilisation) {
        return alignements.stream().anyMatch(alignement -> alignement.getCivilisation().equals(civilisation) && alignement.getPosture() == Posture.AMI);
    }

    public void ajouterTypes(String noms) {
        if (noms.contains("T")) {
            types.add(TypeRegion.TERRESTRE);
        }
        if (noms.contains("M")) {
            types.add(TypeRegion.MARITIME);
        }
        if (noms.contains("C")) {
            types.add(TypeRegion.CAPITAL);
        }
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
        return alignements.equals(region.alignements) && types.equals(region.types) && (armees).equals(region.armees);
    }

    public String getInformations() {
        return MessageFormat.format("Types {0}", types);
    }
}
