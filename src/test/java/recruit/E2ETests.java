package recruit;

import base.BaseRecruit;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class E2ETests extends BaseRecruit {

    private static String candidateToken;
    private static String candidateID;
    private static String positionID;
    private static String applicationID;

    @Test
    public void e2eTest() throws IOException {
        //Create a New Candidate > POST /candidates
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000);
        String email = "candidate" + randomNumber + "@example.com";

        String firstName ="Dima";
        String lastName ="Par";

        JSONObject candidate = new JSONObject();
        candidate.put("firstName", "Dima");
        candidate.put("middleName", "");
        candidate.put("lastName", "Par");
        candidate.put("email", email);
        candidate.put("password", "welcome");
        candidate.put("address", " 70 north edge");
        candidate.put("city", "Castle Rock");
        candidate.put("state", "CO");
        candidate.put("zip", "80104");
        candidate.put("summary", "as long as it matches");


        candidateID =  createNewCandidate(candidate.toString()).jsonPath().getString("id");

        JSONObject credentials = new JSONObject();
        credentials.put("email", email);
        credentials.put("password", "welcome");

        candidateToken = loginWithUser(credentials.toString()).jsonPath().getString("token");
        String adminToken = generateToken();

        JSONObject position = new JSONObject();
        position.put("title","slime man");
        position.put("address","333 Smokey blvd");
        position.put("city","Castle Rock");
        position.put("state","CO");
        position.put("zip","80104");
        position.put("description","should be resistant to weird taste objects");
        String date = LocalDate.now().toString();
        position.put("dateOpen", date);
        position.put("company","korona");

        positionID = createNewPosition(position.toString(), adminToken).jsonPath().getString("id");
        //Create a new application (candidate + position) > POST /applications

        JSONObject application = new JSONObject();
        application.put("candidateId", candidateID);
        application.put("positionId", positionID);
        application.put("dateApplied", date);

        applicationID = createNewApplication(application.toString(), candidateToken).jsonPath().getString("id");

        //Validate the Application > GET /application/id
        Response validateResponse = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/applications/" + applicationID)
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("recruit/verifyResponseSchema.json"))
                .log().body()
                .extract().response();
        JsonPath jsonValidate = validateResponse.jsonPath();



        assertEquals(applicationID, jsonValidate.getString("id"));
        assertEquals(candidateID, jsonValidate.getString("candidateId"));
        assertEquals(positionID, jsonValidate.getString("positionId"));

        assertEquals(firstName, jsonValidate.getString("firstName"));
        assertEquals(lastName, jsonValidate.getString("lastName"));

//        assertEquals(applicationID, jsonValidate.getString("title"));
//        assertEquals(applicationID, jsonValidate.getString("dateApplied"));
//        assertEquals(applicationID, jsonValidate.getString("summary"));
//        assertEquals(applicationID, jsonValidate.getString("description"));
//        assertEquals(applicationID, jsonValidate.getString("dateOpen"));
//        assertEquals(applicationID, jsonValidate.getString("company"));
//
//        assertEquals(applicationID, jsonValidate.getString("candidate_address"));
//        assertEquals(applicationID, jsonValidate.getString("candidate_city"));
//        assertEquals(applicationID, jsonValidate.getString("candidate_state"));
//        assertEquals(applicationID, jsonValidate.getString("candidate_zip"));
//
//        assertEquals(applicationID, jsonValidate.getString("position_address"));
//        assertEquals(applicationID, jsonValidate.getString("position_city"));
//        assertEquals(applicationID, jsonValidate.getString("position_state"));
//        assertEquals(applicationID, jsonValidate.getString("position_zip"));







    }














}
