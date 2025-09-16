package org.mgd.guerres.puniques.coeur.objet;

import org.jetbrains.annotations.NotNull;
import org.mgd.jab.objet.Jo;

import java.util.Comparator;

@SuppressWarnings({"java:S2160", "java:S1210"})
public class TypeRegion extends Jo implements Comparable<TypeRegion> {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof TypeRegion type)) return false;
        return code.equals(type.code);
    }

    @Override
    public int compareTo(@NotNull TypeRegion type) {
        return Comparator.comparing(TypeRegion::getCode).compare(this, type);
    }
}
