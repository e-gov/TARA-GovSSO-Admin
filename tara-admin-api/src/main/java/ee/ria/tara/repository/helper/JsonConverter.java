package ee.ria.tara.repository.helper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Converter
public class JsonConverter implements AttributeConverter<JsonNode, String> {

    private static final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Override
    public String convertToDatabaseColumn(JsonNode object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbValue) {
        try {
            return jsonMapper.readTree(dbValue);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }
}
