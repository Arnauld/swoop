package samples;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import samples.bookshelf.infra.BookshelfException;

public class Json {

    public static <T> T fromJson(byte[] json, Class<T> valueType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, valueType);
        } catch (JsonParseException e) {
            throw new BookshelfException("JSON Deserialization error", e);
        } catch (JsonMappingException e) {
            throw new BookshelfException("JSON Deserialization error", e);
        } catch (IOException e) {
            throw new BookshelfException("JSON Deserialization error", e);
        }
    }
    
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, valueType);
        } catch (JsonParseException e) {
            throw new BookshelfException("JSON Deserialization error", e);
        } catch (JsonMappingException e) {
            throw new BookshelfException("JSON Deserialization error", e);
        } catch (IOException e) {
            throw new BookshelfException("JSON Deserialization error", e);
        }
    }

    public static <T> String toJson(T value) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(value);
        } catch (JsonGenerationException e) {
            throw new BookshelfException("JSON Serialization error", e);
        } catch (JsonMappingException e) {
            throw new BookshelfException("JSON Serialization error", e);
        } catch (IOException e) {
            throw new BookshelfException("JSON Serialization error", e);
        }
    }
}
