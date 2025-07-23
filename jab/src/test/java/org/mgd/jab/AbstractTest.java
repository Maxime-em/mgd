package org.mgd.jab;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.objet.Jo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public abstract class AbstractTest {
    protected void assertJson(JsonElement attendu, JsonElement actuel) {
        Assertions.assertTrue(attendu.isJsonObject());
        Assertions.assertTrue(actuel.isJsonObject());
        Assertions.assertEquals(attendu.getAsJsonObject(), actuel.getAsJsonObject());
    }

    protected void assertFichierVide(Path actuel) throws IOException {
        assertJson(JsonParser.parseString("{}"), JsonParser.parseReader(Files.newBufferedReader(actuel)));
    }

    protected void assertFichier(Path attendu, Path actuel) throws IOException {
        assertJson(JsonParser.parseReader(Files.newBufferedReader(attendu)), JsonParser.parseReader(Files.newBufferedReader(actuel)));
    }

    protected <D extends Dto, O extends Jo<D>> void assertIdentifiantsNonEgales(Collection<O> objets1, Collection<O> objets2) {
        Assertions.assertTrue(objets1.stream().allMatch(objet1 -> objets2.stream().noneMatch(objet1::equals)));
    }

    protected <K, D extends Dto, O extends Jo<D>> void assertIdentifiantsNonEgales(Map<K, O> objets1, Map<K, O> objets2) {
        Assertions.assertTrue(objets1.values().stream().allMatch(objet1 -> objets2.values().stream().noneMatch(objet1::equals)));
    }
}
