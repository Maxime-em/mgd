package org.mgd.gmel.coeur.persistence;

import org.mgd.gmel.coeur.dto.MenuDto;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.jab.persistence.Jao;
import org.mgd.jab.persistence.exception.JaoExecutionException;

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
    protected MenuDto to(Menu menu) {
        MenuDto menuDto = new MenuDto();
        menuDto.setAnnee(menu.getAnnee());
        menuDto.setSemaine(menu.getSemaine());
        menuDto.setFormules(new FormuleJao().decharger(menu.getFormules()));

        return menuDto;
    }

    @Override
    protected void copier(Menu source, Menu cible) throws JaoExecutionException {
        cible.setAnnee(source.getAnnee());
        cible.setSemaine(source.getSemaine());
        cible.getFormules().clear();
        cible.getFormules().addAll(new FormuleJao().dupliquer(source.getFormules()));
    }
}
