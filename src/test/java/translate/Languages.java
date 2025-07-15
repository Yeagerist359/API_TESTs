package translate;

import base.BaseTranslate;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.List;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class Languages extends BaseTranslate {

    @Test
    public void testAllLanguages() {
        // 1. Send Request and Store Response
        Response response = given()
                .queryParam("key", API_KEY)
                .when()
                .get("/languages")
                .then()
                .statusCode(200)
                .extract().response();

        // 2. Extract Data
        List<String> returnedLanguages = response.jsonPath().getList("data.languages.language");
        List<?> fullLanguageObjects = response.jsonPath().getList("data.languages");

        // 3. Validate Expected Languages Exist
        for (String expected : expectedLanguages) {
            assertTrue(returnedLanguages.contains(expected),
                    "Language Code " + expected + " could not be found");
        }

        // 4. Validate Structure
        assertNotNull(response.jsonPath().get("data"), "'data' node is missing");
        assertNotNull(fullLanguageObjects, "'languages' list is missing");
        assertFalse(fullLanguageObjects.isEmpty(), "Languages list should not be empty");
    }
}
