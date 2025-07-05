package base;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class BaseTest {

    protected static final List<String> CANDIDATE_FIELDS = List.of("firstName", "middleName", "lastName", "email", "password", "address", "city","state","zip", "summary");
    protected static final List<String> POSITIONS_FIELDS = List.of("title", "address", "city", "state", "zip", "description", "company");


    @BeforeAll
    public static void setup() {
        baseURI = "http://recruit-1.portnov.com/recruit/api/v1/";
    }

    protected String readFromJsonFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read JSON file: " + path, e);
        }
////    Java Object        method that reads from file      location of said file
//        Files.readString(  Paths.get("src/test/resources/recruit/createCandidate.json")   )

    }



    public String generateToken(){
        String path = "src/test/resources/recruit/studentLogin.json";
        String credentials = readFromJsonFile(path);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(credentials)
                        .when()
                        .post("/login")
                        .then()
                        .statusCode(200).extract().response();
        JsonPath jp = response.jsonPath();

        return jp.getString("token");
    }

    public Response loginWithUser(String body){
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .log().body()
                .extract().response();
        return loginResponse;
    }

    public Response createNewCandidate(String body) {
        Response candidateResponse = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/candidates")
                .then()
                .statusCode(201)
                .extract().response();
        return  candidateResponse;
    }

    public Response createNewPosition(String body, String token) {
        Response positionResponse = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post("/positions")
                .then()
                .statusCode(201)
                .extract().response();
        return positionResponse;
    }

    public Response createNewApplication(String body, String token) {
        Response applicationResponse = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post("/applications")
                .then()
                .statusCode(201)
                .extract().response();
        return applicationResponse;
    }




}
