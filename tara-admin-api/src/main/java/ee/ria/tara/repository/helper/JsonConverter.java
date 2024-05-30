package ee.ria.tara.repository.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbValue) {
        try {
            return jsonMapper.readTree(dbValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
