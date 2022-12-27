package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.MenuDto;
import org.mgd.gmel.coeur.objet.Menu;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.Ad;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.WeekFields;

public class MenuAd extends Ad<MenuDto, Menu, MenuAf> {
    private static final DateTimeFormatter formateur = new DateTimeFormatterBuilder().appendPattern("YYYY-ww").toFormatter();

    public MenuAd(Path dossier) {
        super(dossier);
    }

    @Override
    protected MenuAf access(Path source) {
        return new MenuAf(source);
    }

    public Menu menu(LocalDate reference) throws IOException, JaoExecutionException, JaoParseException {
        return access(reference.format(formateur),
                String.format("{\"annee\":%d,\"semaine\":%d,\"formules\":[]}",
                        reference.getYear(),
                        reference.get(WeekFields.ISO.weekOfYear())))
                .jo();
    }
}
