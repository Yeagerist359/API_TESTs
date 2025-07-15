package base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class BaseTranslate {

    // API key read from environment variable
    protected static final String API_KEY = System.getenv("API_KEY");

    // Jackson ObjectMapper for JSON parsing
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    // Expected language codes loaded once before all tests
    protected static List<String> expectedLanguages;

    @BeforeAll
    public static void setupBase() {
        baseURI = "https://translation.googleapis.com/language/translate/v2";

        expectedLanguages = readFromJsonFile(
                "src/test/resources/translate/languages.json",
                new TypeReference<List<String>>() {}
        );
    }
    // Generic method to parse JSON file into any structure (List, Map, etc.)
    protected static <T> T readFromJsonFile(String path, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(new File(path), typeReference);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to parse JSON file: " + path, e);
        }
    }

    protected static void writeJsonToFile(Object data, String path) {
        try {
            File file = new File(path);
            file.getParentFile().mkdirs(); // Ensure the directory exists
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write JSON file: " + path, e);
        }
    }

    // Helper method to translate a sentence into a target language using Google Translate API.
    protected static String translate(String sentence, String targetLang) {
        return given()
                .queryParam("key", API_KEY)
                .queryParam("q", sentence)
                .queryParam("target", targetLang)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("data.translations[0].translatedText");
    }

    protected static <K, V> Map.Entry<K, V> getRandomEntry(Map<K, V> map) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
        Collections.shuffle(entries);
        return entries.get(0);
    }

}