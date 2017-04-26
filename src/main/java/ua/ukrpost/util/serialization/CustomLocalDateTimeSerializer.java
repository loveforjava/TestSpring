package ua.ukrpost.util.serialization;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class CustomLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    
    @Override
    public void serialize(LocalDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializers)
            throws IOException {
        jsonGenerator.writeString(value.format(ISO_LOCAL_DATE_TIME));
    }
}
