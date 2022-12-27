package org.mgd.temps;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.mgd.commun.TypeRepas;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalRepasAdapter extends TypeAdapter<LocalRepas> {
    @Override
    public void write(JsonWriter writer, LocalRepas repas) throws IOException {
        if (repas == null) {
            writer.nullValue();
            return;
        }
        writer.value(MessageFormat.format("{0}#{1}", DateTimeFormatter.ISO_LOCAL_DATE.format(repas.getJour()), repas.getType()));
    }

    @Override
    public LocalRepas read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        String[] repasLu = reader.nextString().split("#");
        if (repasLu.length < 2) {
            return null;
        }
        return LocalRepas.pour(LocalDate.parse(repasLu[0], DateTimeFormatter.ISO_LOCAL_DATE), TypeRepas.valueOf(repasLu[1]));
    }
}
