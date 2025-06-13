package org.mgd.gmel.coeur.dto;

import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.gmel.coeur.persistence.MenuJao;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.ReferenceDto;

import java.util.List;

public class AgendaDto extends Dto {
    private List<ReferenceDto<MenuDto, Menu, MenuJao>> menus;

    public List<ReferenceDto<MenuDto, Menu, MenuJao>> getMenus() {
        return menus;
    }

    public void setMenus(List<ReferenceDto<MenuDto, Menu, MenuJao>> menus) {
        this.menus = menus;
    }
}
