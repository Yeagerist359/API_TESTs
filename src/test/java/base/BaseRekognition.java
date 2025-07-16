package base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.internal.util.IOUtils;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;

public class BaseRekognition {

    protected static final String API_KEY = System.getenv("API_KEY");

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    protected static <T> T readFromJsonFile(String path, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(new File(path), typeReference);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to parse JSON file: " + path, e);
        }
    }

    protected static final String TestingText = readFromJsonFile(
            "src/test/resources/speech_text/TestingText.json",
            new TypeReference<String>() {}
    );

    protected static final List<String> VOICES_EN_US = List.of(
            "Danielle", "Gregory", "Ivy",
            "Joanna", "Kendra", "Kimberly",
            "Salli", "Joey", "Justin",
            "Kevin", "Matthew", "Ruth",
            "Stephen", "Patrick");

    protected Map<String, String> getDefaultTextBody() {
        Map<String, String> body = new HashMap<>();
        body.put("text", TestingText);
        return body;
    }



    @BeforeAll
    static void setup(){
        baseURI = "https://image-ai.portnov.com/";
    }

    protected String base64Encode(String path) {
        byte[] imageByte;
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
            imageByte = IOUtils.toByteArray(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(imageByte);
    }

}
