package image_ai;

import base.BaseRekognition;
import io.restassured.http.ContentType;
import io.restassured.internal.util.IOUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class DetectLabelsTest extends BaseRekognition {

    List<String> labels = List.of("Head", "Person", "Face", "Smile", "Dimples");

    @Test
    public void testDetectLabels(){
        //Step 0 convert image to byte
        byte[] imageByte;
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("images/arnie-1.jpg");
            imageByte = IOUtils.toByteArray(stream);
        } catch (IOException e){
           throw new RuntimeException(e);
        }
        //Step 1 convert image > byte> base64 String
        String imageb64 = Base64.getEncoder().encodeToString(imageByte);
        //Step 2 create Body for Request

        Map<String, Object> body = new HashMap<>();
        body.put("base64Image", imageb64);
        body.put("maxLabels", 5);
        body.put("maxConfidence", 1);
        //Step 3 Send Request, get Response
        Response response = given()
                .contentType(ContentType.JSON)
                .header("X-Api-Key", API_KEY)
                .body(body)
                .when()
                .post("/api/Image/detect-labels")
                .then()
                .statusCode(200)
                .extract().response();
        response.prettyPrint();

        //Step 4 assert Something: name

        String responseString = response.asString();
        assertTrue(responseString.contains("Person"));
    }

    @Test
    public void testDetectLabelsMultiple(){
        //Step 0 convert image to byte
        byte[] imageByte;
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("images/arnie-1.jpg");
            imageByte = IOUtils.toByteArray(stream);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        //Step 1 convert image > byte> base64 String
        String imageb64 = Base64.getEncoder().encodeToString(imageByte);
        //Step 2 create Body for Request

        Map<String, Object> body = new HashMap<>();
        body.put("base64Image", imageb64);
        body.put("maxLabels", 5);
        body.put("maxConfidence", 1);
        //Step 3 Send Request, get Response
        Response response = given()
                .contentType(ContentType.JSON)
                .header("X-Api-Key", API_KEY)
                .body(body)
                .when()
                .post("/api/Image/detect-labels")
                .then()
                .statusCode(200)
                .extract().response();
        response.prettyPrint();

        //Step 4 assert Something: name
        List<String> list = response.jsonPath().getList("name");

        for(String l : labels ){
            assertTrue(list.contains(l), l + " not found");
        }

        System.out.println(list);

    }

}
