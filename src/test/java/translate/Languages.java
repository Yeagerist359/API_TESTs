package translate;

import base.BaseTranslate;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class Languages extends BaseTranslate {

    List<String> expectedLanguages =readFromJsonFile("src/test/resources/translate/languages.json");


    @Test
    public void testAllLanguages() {
//      1  Send Request and Store value
        Response response = given()
                .queryParam("key", API_KEY)
                .when()
                .get("/languages")
                .then()
                .statusCode(200)
                .extract().response();
//        response.prettyPrint();

//        2    Convert the response into a usable object (List)
        List<String> returnedLanguages = response.jsonPath().getList("data.languages.language");

//        3.   assert something is present or exists in the response
        for (String code: expectedLanguages) {
            assertTrue(returnedLanguages.contains(code), "Language Code " + code +" could not be found");
            System.out.println("Found " + code + " Successfully");
        }

    }


}
