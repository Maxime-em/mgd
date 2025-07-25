package org.mgd.jab.objet;

import java.util.List;
import java.util.Map;

public class Personne extends Jo {
    private final List<Jeu> jeux = new JocArrayList<>(this);
    private final Map<Integer, Livre> livres = new JocHashMap<>(this);
    private final Joc<Long> score = new Joc<>(this);

    public List<Jeu> getJeux() {
        return jeux;
    }

    public Map<Integer, Livre> getLivres() {
        return livres;
    }

    public Long getScore() {
        return score.get();
    }

    public void setScore(Long score) {
        this.score.set(score);
    }

    @Override
    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Personne personne)) return false;
        return ((JocArrayList<Jeu>) jeux).idem(personne.jeux) && ((JocHashMap<Integer, Livre>) livres).idem(personne.livres) && score.idem(personne.score);
    }
}
