package org.mgd.jab.dto.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.mgd.jab.JabSauvegarde;
import org.mgd.jab.persistence.Jao;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Adaptateur utilisé notamment dans {@link Jao} et {@link JabSauvegarde}.
 */
public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public void write(JsonWriter out, LocalDate date) throws IOException {
        if (Objects.isNull(date)) {
            out.nullValue();
        } else {
            out.value(date.format(formatter));
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return LocalDate.parse(in.nextString(), formatter);
        }
    }
}
