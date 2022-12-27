package org.mgd.jab.objet;

import com.google.gson.annotations.JsonAdapter;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.utilitaire.Jos;

import java.util.Objects;

/**
 * Classe a utilisé pour définir les champs d'un objet métier de type {@link Jo}.
 * Cette classe permet de gérer notamment les parents d'un objet métier et de démarrer la sauvegarde dans le fichier JSON.
 * Le {@link Joc#contenant} est l'objet de type {@link Jo} qui contient le champ.
 * Le {@link Joc#contenu} est la valeur du champ.
 *
 * @param <T> Type du champ métier
 * @author Maxime
 */
@JsonAdapter(JocJsonSerializer.class)
public class Joc<T> {
    protected final Jo<? extends Dto> contenant;
    protected T contenu;

    public Joc(Jo<? extends Dto> contenant) {
        this.contenant = contenant;
    }

    public T get() {
        return this.contenu;
    }

    public void set(T contenu) {
        if (this.contenu != contenu) {
            if (!Objects.isNull(contenu) && Objects.isNull(this.contenu)) {
                this.contenant.ajouterEnfant(contenu);
                this.contenu = contenu;
                this.contenant.sauvegarder();
            } else if (!Objects.isNull(contenu) && !contenu.equals(this.contenu)) {
                this.contenant.ajouterEnfant(contenu);
                this.contenant.enleverEnfant(this.contenu);
                this.contenu = contenu;
                this.contenant.sauvegarder();
            } else if (Objects.isNull(contenu)) {
                this.contenant.enleverEnfant(this.contenu);
                this.contenu = null;
                this.contenant.sauvegarder();
            }
        }
    }

    public boolean idem(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Joc<?> joc)) return false;
        return Jos.idem(contenu, joc.contenu);
    }

    @Override
    public boolean equals(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof Joc<?> joc)) return false;
        return Objects.equals(contenu, joc.contenu);
    }

    @Override
    public int hashCode() {
        return contenu != null ? contenu.hashCode() : 0;
    }
}
