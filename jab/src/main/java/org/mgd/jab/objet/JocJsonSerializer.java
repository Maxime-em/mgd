package org.mgd.jab.objet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Utiliser pour la sauvegarde des objets métiers de type {@link Jo}.
 *
 * @param <T> Type de l'objet à sérialiser
 * @author Maxime
 */
public class JocJsonSerializer<T> implements JsonSerializer<Joc<T>> {
    private static final Gson gson = new Gson();

    @Override
    public JsonElement serialize(Joc<T> joc, Type type, JsonSerializationContext context) {
        return gson.toJsonTree(joc.contenu);
    }
}
