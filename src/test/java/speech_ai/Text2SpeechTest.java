package speech_ai;

import base.BaseRekognition;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Text2SpeechTest extends BaseRekognition {
    RequestSpecification spec;

    @BeforeEach
    public void amend(){
        spec = new RequestSpecBuilder()
                .setBaseUri("https://image-ai.portnov.com/api/Speech")
                .setContentType(ContentType.JSON)
                .addHeader("X-APi-Key", API_KEY)
                .build();
    }

    @Test
    public void text2speechTest(){

        Map<String, String> body = new HashMap<>();
        body.put("text", "Who took mah cabbage in this wonderfull house");

        Response response = given()
                .spec(spec)
                .queryParam("voice", "Justin")
                .body(body)
                .when()
                .post("/convert-text-to-speech")
                .then()
                .statusCode(200)
                .extract().response();

        response.prettyPrint();

        byte[] bytes = response.asByteArray();

        try (FileOutputStream fos = new FileOutputStream("text.mp3")){
            IOUtils.write(bytes, fos);

        } catch (IOException e) {
            throw new UncheckedIOException("Failed writing text.mp3", e);
        }

        assertTrue(bytes.length > 0);


    }
}
