package org.mgd.gmel.pdf.pagination;

import org.mgd.gmel.coeur.objet.Agenda;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.gmel.pdf.pagination.producteur.MenuProducteur;
import org.mgd.pam.pagination.Paginateur;

import java.util.List;

public class AgendaPaginateur extends Paginateur<Agenda, Menu> {
    public AgendaPaginateur() {
        super(new MenuProducteur());
    }

    @Override
    protected List<Menu> lister(Agenda agenda) {
        return agenda.getMenus().stream().sorted((menu1, menu2) -> menu1.getAnnee().equals(menu2.getAnnee())
                ? menu1.getSemaine().compareTo(menu2.getSemaine())
                : menu1.getAnnee().compareTo(menu2.getAnnee())).toList();
    }
}
