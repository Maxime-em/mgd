package org.mgd.jab.dto.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Adaptateur utilisé notamment dans par {@link org.mgd.jab.JabSauvegarde#gsonSauvegarde}.
 *
 * @author Maxime
 */
public class PathAdapter extends TypeAdapter<Path> {
    @Override
    public void write(JsonWriter out, Path chemin) throws IOException {
        if (Objects.isNull(chemin)) {
            out.nullValue();
        } else {
            out.value(chemin.toAbsolutePath().toString());
        }
    }

    @Override
    public Path read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return Path.of(in.nextString());
        }
    }
}
