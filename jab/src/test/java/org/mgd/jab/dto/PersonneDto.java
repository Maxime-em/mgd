package org.mgd.jab.dto;

import java.util.List;
import java.util.Map;

public class PersonneDto extends Dto {
    private List<JeuDto> jeux;
    private Map<Integer, LivreDto> livres;
    private Long score;

    public List<JeuDto> getJeux() {
        return jeux;
    }

    public void setJeux(List<JeuDto> jeux) {
        this.jeux = jeux;
    }

    public Map<Integer, LivreDto> getLivres() {
        return livres;
    }

    public void setLivres(Map<Integer, LivreDto> livres) {
        this.livres = livres;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
