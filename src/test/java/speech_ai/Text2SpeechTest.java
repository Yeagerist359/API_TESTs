package speech_ai;

import base.BaseRekognition;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileOutputStream;

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
    public void text2speechTest() {

        Map<String, String> body = getDefaultTextBody();

        String outputDirPath = "src/test/resources/speech_text";
        File outputDir = new File(outputDirPath);


        for (String voice : VOICES_EN_US) {
            try {
                Response response = given()
                        .spec(spec)
                        .queryParam("voice", voice)
                        .body(body)
                        .when()
                        .post("/convert-text-to-speech");

                int statusCode = response.getStatusCode();
                if (statusCode != 200) {
                    System.err.println("Failed for voice " + voice + " - Status: " + statusCode);
                    continue;
                }

                byte[] bytes = response.asByteArray();
                String fileName = outputDirPath + "/" + voice + "_speech.mp3";

                try (FileOutputStream fos = new FileOutputStream(fileName)) {
                    IOUtils.write(bytes, fos);
                    System.out.println("Saved: " + fileName);
                }

                assertTrue(bytes.length > 0, "Audio data should not be empty for voice: " + voice);

            } catch (Exception e) {
                System.err.println("Exception for voice " + voice + ": " + e.getMessage());
            }
        }
    }
}
