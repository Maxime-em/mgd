package org.mgd.guerres.puniques.coeur.objet;

import org.mgd.guerres.puniques.coeur.commun.TypeUnite;
import org.mgd.jab.objet.Jo;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("java:S2160")
public class Reserve extends Jo {
    private final Set<Unite> unites = new TreeSet<>();
    private final Map<TypeUnite, Integer> nombresUnitesMaximales = new EnumMap<>(TypeUnite.class);

    public Set<Unite> getUnites() {
        return unites;
    }

    public Map<TypeUnite, Integer> getNombresUnitesMaximales() {
        return nombresUnitesMaximales;
    }

    public String getInformations() {
        return Arrays.stream(TypeUnite.values())
                .filter(type -> nombresUnitesMaximales.getOrDefault(type, 0) > 0)
                .map(type ->
                        MessageFormat.format("{0} ({1}/{2})",
                                type.getNom(),
                                unites.stream().filter(unite -> unite.getType() == type).count(),
                                nombresUnitesMaximales.get(type)))
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Reserve reserve)) return false;
        return unites.equals(reserve.unites) && nombresUnitesMaximales.equals(reserve.nombresUnitesMaximales);
    }
}
