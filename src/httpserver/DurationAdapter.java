package httpserver;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {

        if (duration == null) {
            jsonWriter.value("null");
        } else {
            jsonWriter.value(duration.toSeconds());
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        if (value.equals("null")) {
            return null;
        } else {
            return Duration.ofSeconds(Long.parseLong(value));
        }
    }
}
