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

public class Speech2TextTest extends BaseRekognition {
    RequestSpecification spec;

    @BeforeEach
    public void amend() {
        spec = new RequestSpecBuilder()
                .setBaseUri("https://image-ai.portnov.com/api/Speech")
                .addHeader("X-APi-Key", API_KEY)
                .build();
    }

    @Test
    public void speech2text() {
        File folder;
        try {
            folder = new File(getClass().getClassLoader().getResource("speech_text").toURI());
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load speech_text directory", e);
        }
//        File mp3 = new File(getClass().getClassLoader().getResource("speech_text/Ivy_speech.mp3").getFile());
        File[] mp3Files = folder.listFiles(file -> file.getName().toLowerCase().endsWith(".mp3"));


        for (File mp3 : mp3Files) {
            try {
                System.out.println("🔁 Testing file: " + mp3.getName());

                Response response = given()
                        .spec(spec)
                        .contentType(ContentType.MULTIPART)
                        .multiPart("audioFile", mp3)
                        .when()
                        .post("/convert-speech-to-text")
                        .then()
                        .statusCode(200)
                        .extract().response();

                String status = response.jsonPath().getString("jsonResponse.status");
                if (!"COMPLETED".equals(status)) {
                    System.err.println("❌ Status mismatch for file: " + mp3.getName() + " (got: " + status + ")");
                    continue;
                }

                String transcript = response.jsonPath().getString("jsonResponse.results.transcripts[0].transcript");
                if (!TestingText.equals(transcript)) {
                    System.err.println("❌ Text mismatch for file: " + mp3.getName());
                    System.err.println("Expected: " + TestingText);
                    System.err.println("Actual:   " + transcript);
                    continue;
                }

                System.out.println("✅ Passed: " + mp3.getName());

            } catch (Exception e) {
                System.err.println("❌ Exception while processing file: " + mp3.getName());
                e.printStackTrace();
            }
        }
    }
}
