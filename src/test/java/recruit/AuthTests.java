package recruit;

import base.BaseRecruit;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthTests extends BaseRecruit {

    private static String token;

    @Test
    @Order(1)
    public void testLogin() throws IOException {
//        JSONObject json = new JSONObject();
//        json.put("email", "student@example.com");
//        json.put("password", "welcome");
        String path = "src/test/resources/studentLogin.json";
        String credentials = readFromJsonFile(path);

        System.out.println(credentials);


        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(credentials)
                        .when()
                        .post("/login")
                        .then()
                        .statusCode(200).extract().response();
        JsonPath jp = response.jsonPath();
        token = jp.getString("token");

        response.prettyPrint();
    }

    @Test
    @Order(2)
    public void testVerifyWithToken() {

        Response response = given()
                .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")))
                .header("Authorization", "Bearer " + token )
                .when()
                .post("/verify")
                .then()
                .statusCode(200)
                .extract().response();
        response.prettyPrint();


    }


}