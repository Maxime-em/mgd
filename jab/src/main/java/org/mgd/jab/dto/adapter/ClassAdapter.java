package org.mgd.jab.dto.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.mgd.jab.dto.exception.DtoAdaptateurRuntimeException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * Adaptateur utilisé notamment dans par {@link org.mgd.jab.JabSauvegarde#gsonSauvegarde}.
 *
 * @author Maxime
 */
public class ClassAdapter extends TypeAdapter<Class<?>> {
    @Override
    public void write(JsonWriter out, Class<?> classe) throws IOException {
        if (Objects.isNull(classe)) {
            out.nullValue();
        } else {
            out.value(classe.getName());
        }
    }

    @Override
    public Class<?> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            try {
                return Class.forName(in.nextString());
            } catch (ClassNotFoundException e) {
                throw new DtoAdaptateurRuntimeException(MessageFormat.format("Impossible de récupérer la classe nommer {0}", in.nextString()), e);
            }
        }
    }
}
