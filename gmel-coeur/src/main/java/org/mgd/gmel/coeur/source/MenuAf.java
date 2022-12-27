package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.MenuDto;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.gmel.coeur.persistence.MenuJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class MenuAf extends Af<MenuDto, Menu> {
    public MenuAf(Path fichier) {
        super(new MenuJao(), fichier);
    }
}
