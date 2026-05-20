package org.mgd.jab.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Données JSON d'un super-héros de l'univers DC Comics.
 */
public class HerosDto extends Dto {
    public String nom;
    public String alias;
    public String ville;
    public LocalDate premiereApparition;
    public List<String> pouvoirs = new ArrayList<>();
    public HerosDto mentor;
    public List<HerosDto> allies = new ArrayList<>();
}
