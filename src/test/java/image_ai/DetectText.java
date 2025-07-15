package image_ai;

import base.BaseRekognition;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class DetectText extends BaseRekognition {

    @Test
    public void testDetectText(){
        String b64 = base64Encode("images/Untitled.png");
        Map<String, Object> body = new HashMap<>();
        body.put("base64Image", b64);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("X-Api-Key", API_KEY)
                .body(body)
                .when()
                .post("/api/Image/detect-text")
                .then()
                .statusCode(200)
                .extract().response();
        

        List<String> list = response.jsonPath().getList("detectedText");
        System.out.println(list);
    }
}
