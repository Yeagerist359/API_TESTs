package cookies;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Cookies {

    @BeforeAll
    static void setup(){
        baseURI = "https://image-ai.portnov.com/api/Cookie";

    }

    @Test
    public void cookiesTest(){

        Response responseGet = given()
                .when()
                .get("get")
                .then()
                .extract().response();
        responseGet.prettyPrint();
        Map<String, String> cookies = responseGet.getCookies();
        System.out.println(cookies);
        cookies.get("test");

        Response responseCheck = given()
                .cookies(cookies)
                .when()
                .get("check")
                .then()
                .extract().response();
        responseCheck.prettyPrint();

        assertEquals("Cookie is valid", responseCheck.asString());

    }


}
