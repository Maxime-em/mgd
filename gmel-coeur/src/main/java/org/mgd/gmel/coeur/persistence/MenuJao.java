package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.MenuDto;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.Verifications;
import org.mgd.jab.utilitaire.exception.VerificationException;

/**
 * Classe permettant de manipuler les objets métiers de type {@link Menu} et de les charger depuis un système de
 * fichiers JSON via la classe de transfert {@link MenuDto}.
 *
 * @author Maxime
 */
public class MenuJao extends Jao<MenuDto, Menu> {
    public MenuJao() {
        super(MenuDto.class, Menu.class);
    }

    @Override
    public MenuDto dto(Menu menu) {
        MenuDto menuDto = new MenuDto();
        menuDto.setAnnee(menu.getAnnee());
        menuDto.setSemaine(menu.getSemaine());
        menuDto.setFormules(new FormuleJao().decharger(menu.getFormules()));

        return menuDto;
    }

    @Override
    public void enrichir(MenuDto dto, Menu menu) throws JaoExecutionException, JaoParseException, VerificationException {
        Verifications.nonNull(dto.getFormules(), "Les formules d''un menu devrait être une liste éventuellement vide");
        Verifications.nonMultiple(
                dto.getFormules(),
                (formuleDto, formuleDto2) -> formuleDto.getRecette().equals(formuleDto2.getRecette()),
                "Les recettes des formules doivent être unique"
        );
        Verifications.nonAnnee(dto.getAnnee(), "L''année doit être comprise entre {1} et {2}");
        Verifications.nonSemaine(dto.getSemaine(), dto.getAnnee(), "Le numéro de la semaine doit être compris entre {1} et {2}");

        menu.setAnnee(dto.getAnnee());
        menu.setSemaine(dto.getSemaine());
        menu.getFormules().addAll(new FormuleJao().charger(dto.getFormules(), menu));
    }

    @Override
    protected void copier(Menu source, Menu cible) throws JaoExecutionException, JaoParseException {
        cible.setAnnee(source.getAnnee());
        cible.setSemaine(source.getSemaine());
        cible.getFormules().clear();
        cible.getFormules().addAll(new FormuleJao().dupliquer(source.getFormules()));
    }
}
