package translate;

import base.BaseTranslate;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;


public class Detect extends BaseTranslate {


    @Test
    public void detectLanguage(){
        String translate = "this is very interesting test";

        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("q", translate)
                .when()
                .post("/detect")
                .then()
                .statusCode(200)
                .extract().response();
        response.prettyPrint();

        Map<String, Object> detections = response.jsonPath().getMap("data.detections[0][0]");
        System.out.println(detections);


        assertEquals("en", detections.get("language"));

        boolean isReliable = (Boolean) detections.get("isReliable");
        assertFalse(isReliable, "Should be false" );

        Number confidenceNumber = (Number) detections.get("confidence");
        double confidence = confidenceNumber.doubleValue();
        assertTrue(confidence >= 0.9, "Confidence level is too low");
    }
}
