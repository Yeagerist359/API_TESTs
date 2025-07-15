package base;

import io.restassured.internal.util.IOUtils;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static io.restassured.RestAssured.baseURI;

public class BaseRekognition {

    protected static final String API_KEY = System.getenv("API_KEY");

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
