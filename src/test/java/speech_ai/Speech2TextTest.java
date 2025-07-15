package speech_ai;

import base.BaseRekognition;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Speech2TextTest extends BaseRekognition {
    RequestSpecification spec;

    @BeforeEach
    public void amend(){
        spec = new RequestSpecBuilder()
                .setBaseUri("https://image-ai.portnov.com/api/Speech")
                .addHeader("X-APi-Key", API_KEY)
                .build();
    }

    @Test
    public void speech2text(){
        File mp3 = new File(getClass().getClassLoader().getResource("test_speech.mp3").getFile());

        Response response = given()
                .spec(spec)
                .contentType(ContentType.MULTIPART)
                .multiPart("audioFile", mp3)
                .when()
                .post("/convert-speech-to-text")
                .then()
                .statusCode(200)
                .extract().response();

//        response.prettyPrint();

        String status = response.jsonPath().getString("jsonResponse.status");
        assertEquals("COMPLETED",status, "status is not matching");

        String transcript = response.jsonPath().getString("jsonResponse.results.transcripts[0].transcript");
        assertEquals("Hello, this is a test for text to speech conversion.", transcript, "text is not matching");


    }
}
