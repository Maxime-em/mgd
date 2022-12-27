package org.mgd.gmel.coeur.dto;

import org.mgd.jab.dto.Dto;

import java.util.List;

public class AgendaDto extends Dto {
    private List<MenuDto> menus;

    public List<MenuDto> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuDto> menus) {
        this.menus = menus;
    }
}
