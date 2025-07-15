package translate;

import base.BaseTranslate;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class Detect extends BaseTranslate {

    @Test
    public void detectLanguage() {
        // Load translated text: Map<languageCode, translatedText>
        Map<String, String> translations = readFromJsonFile(
                "src/test/resources/translate/translatedText.json",
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {}
        );

        // Select a random translated sentence
        Map.Entry<String, String> randomEntry = getRandomEntry(translations);
        String expectedLang = randomEntry.getKey();
        String sentence = randomEntry.getValue();

        // Send detect language request
        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("q", sentence)
                .when()
                .post("/detect")
                .then()
                .statusCode(200)
                .extract().response();

        // Extract detection result
        Map<String, Object> detection = response.jsonPath().getMap("data.detections[0][0]");

        // Debug output
        System.out.printf("Expected Language: %s%nDetected: %s%nSentence: %s%n",
                expectedLang, detection, sentence);

        // Assertions
        assertEquals(expectedLang, detection.get("language"), "Language detection mismatch");

        assertTrue(detection.containsKey("isReliable"), "Missing 'isReliable' field");
        assertTrue(detection.containsKey("confidence"), "Missing 'confidence' field");

        double confidence = ((Number) detection.get("confidence")).doubleValue();
        assertTrue(confidence >= 0.5, "Low confidence: " + confidence);
    }
}
