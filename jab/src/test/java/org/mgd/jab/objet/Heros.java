package org.mgd.jab.objet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Super-héros de l'univers DC Comics, objet métier de test.
 */
public class Heros extends Jo {
    private final Joc<Heros> mentor = new Joc<>(this);
    private final JocArrayList<Heros> allies = new JocArrayList<>(this);
    private String nom;
    private String alias;
    private String ville;
    private LocalDate premiereApparition;
    private List<String> pouvoirs = new ArrayList<>();

    public Heros() {
        super();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
        sauvegarder();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
        sauvegarder();
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
        sauvegarder();
    }

    public LocalDate getPremiereApparition() {
        return premiereApparition;
    }

    public void setPremiereApparition(LocalDate premiereApparition) {
        this.premiereApparition = premiereApparition;
        sauvegarder();
    }

    public List<String> getPouvoirs() {
        return pouvoirs;
    }

    public void setPouvoirs(List<String> pouvoirs) {
        this.pouvoirs = new ArrayList<>(pouvoirs);
        sauvegarder();
    }

    public Joc<Heros> getMentor() {
        return mentor;
    }

    public JocArrayList<Heros> getAllies() {
        return allies;
    }

    @Override
    public boolean idem(Object objet) {
        return objet instanceof Heros autre
                && Objects.equals(nom, autre.nom)
                && Objects.equals(alias, autre.alias)
                && Objects.equals(ville, autre.ville)
                && Objects.equals(premiereApparition, autre.premiereApparition)
                && Objects.equals(pouvoirs, autre.pouvoirs)
                && Objects.equals(mentor.get(), autre.mentor.get())
                && Objects.equals(new ArrayList<>(allies), new ArrayList<>(autre.allies));
    }
}
