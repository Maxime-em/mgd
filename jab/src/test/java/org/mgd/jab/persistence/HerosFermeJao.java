package org.mgd.jab.persistence;

/**
 * Accès aux données des héros dont le constructeur n'est pas public : le fournisseur ne peut pas être instancié par réflexion.
 */
public class HerosFermeJao extends HerosJao {
    private HerosFermeJao() {
        super();
    }
}
