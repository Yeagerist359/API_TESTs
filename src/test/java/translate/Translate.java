package translate;

import base.BaseTranslate;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Translate extends BaseTranslate {

    @Test
    public void translateTest(){
        String text = "тест про курицу" ;
        String language = "en";

        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("q", text)
                .queryParam("target", language)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();
//        response.prettyPrint();

        String translatedText = response.jsonPath().getString("data.translations[0].translatedText");
        String detectedSourceLanguage = response.jsonPath().getString("data.translations[0].detectedSourceLanguage");


        assertEquals("chicken test", translatedText);
        assertEquals("ru", detectedSourceLanguage);
    }

    @Test
    public void translateTestPayload(){
        String[] text = {"тест про курицу", "тест про нарезанный лук", "тест про нарезанный холодное пиво"} ;
        String language = "en";

        Response response = given()
                .queryParam("key", API_KEY)
                .queryParam("q", text)
                .queryParam("target", language)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();
        response.prettyPrint();

        

//        String translatedText = response.jsonPath().getString("data.translations[0].translatedText");
//        String detectedSourceLanguage = response.jsonPath().getString("data.translations[0].detectedSourceLanguage");
//
//
//        assertEquals("chicken test", translatedText);
//        assertEquals("ru", detectedSourceLanguage);
    }


}
