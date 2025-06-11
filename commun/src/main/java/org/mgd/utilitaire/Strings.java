package org.mgd.utilitaire;

import java.util.Objects;
import java.util.regex.Pattern;

public class Strings {
    private Strings() {
        throw new IllegalStateException("Classe utilitaire.");
    }

    public static String premierCaractereMajuscule(String str) {
        Objects.requireNonNull(str);
        return Pattern.compile("\\S").matcher(str).replaceFirst(result -> result.group().toUpperCase());
    }
}
