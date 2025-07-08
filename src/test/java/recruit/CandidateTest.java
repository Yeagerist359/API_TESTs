package recruit;

import base.BaseRecruit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CandidateTest  extends BaseRecruit {

    // generate token for each test
    @BeforeEach
    public void setupToken() {
        token = generateToken();
    }

    // List of reusable position field names for extracting and verifying JSON body
//    public static final List<String> CANDIDATE_FIELDS = List.of("firstName", "middleName", "lastName", "email", "password", "address", "city","state","zip", "summary");


    private static Integer candidateID;
    private static String token;
    private static Map<String,String> expectedFields = new HashMap<>();
    private static Map<String,String> updatedFields = new HashMap<>();


    private void assertFieldsMatch(Map<String, String> expected , Response response){
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            String key = entry.getKey();
            String expectedValue = entry.getValue();

            // Skip sensitive fields
            if ("password".equalsIgnoreCase(key) || "email".equalsIgnoreCase(key)) {
                continue;
            }

            String actualValue = response.jsonPath().getString(key);
            assertEquals(expectedValue,actualValue,"Mismatch on field: " + key);
        }
    }


    @Test
    // 1st test to create candidate and storing candidateID and expectedFields of a body
    @Order(1)
    public void createCandidate(){

        String canBody = readFromJsonFile("src/test/resources/recruit/createCandidate.json");
        Response response = createNewCandidate(canBody);
        assertEquals(201, response.getStatusCode());
        candidateID = response.jsonPath().getInt("id");

        // Extract and store all response fields as expectedFields
        for(String field : CANDIDATE_FIELDS){
            expectedFields.put(field, response.jsonPath().getString(field));
        }
        System.out.println("Created Candidate ID: " + candidateID);

    }

    @Test
    // 2nd test to read created candidate
    @Order(2)
    public void readCandidate(){
        Response response = given()
                .contentType(ContentType.JSON)
                .pathParams("id", candidateID)
                .when()
                .get("/candidates/{id}")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("recruit/verifyResponseSchema.json"))
                .extract().response();

        assertEquals(candidateID, Integer.valueOf(response.jsonPath().getInt("id")));
        assertTrue(response.getHeader("Content-Type").contains("application/json"), "Expected JSON content type");

        //comparing "expectedFields" to a response that we got from get request
        assertFieldsMatch(expectedFields, response);



    }

    @Test
    //3rd test updating created candidate with new json file excluding email and password
    @Order(3)
    public void updateCandidate(){
        String body = readFromJsonFile("src/test/resources/recruit/putCandidate.json");

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer "+token)
                .body(body)
                .pathParams("id", candidateID)
                .when()
                .put("/candidates/{id}")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("recruit/verifyResponseSchema.json"))
                .extract().response();

        assertEquals(candidateID, Integer.valueOf(response.jsonPath().getInt("id")));
        System.out.println("Updated Position ID: " + candidateID);
        assertTrue(response.getHeader("Content-Type").contains("application/json"), "Expected JSON content type");

        //making new body into "updatedFields"
        for (String newField : CANDIDATE_FIELDS){
            String value = response.jsonPath().getString(newField);

            // Exclude password and email from general comparison
            if ("password".equalsIgnoreCase(newField)) continue;

            // Do NOT include email in updatedFields
            if ("email".equalsIgnoreCase(newField)) continue;

            updatedFields.put(newField,value);
        }

        // Verifying updated response fields match the expected updated values
        assertFieldsMatch(updatedFields, response);

        // this should check that email was not changed
        String updatedEmail = response.jsonPath().getString("email");
        String originalEmail = expectedFields.get("email");
        assertEquals(originalEmail, updatedEmail, "Email should NOT be updated in PUT request");
    }


    @Test
    @Order(4)
    public void patchCandidate() throws IOException {
        String patchbody = readFromJsonFile("src/test/resources/recruit/patchCandidate.json");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> allFieldsToPatch = objectMapper.readValue(patchbody, new TypeReference<Map<String, String>>() {});

        // Loop through each field to patch one by one
        for(Map.Entry<String,String> entry : allFieldsToPatch.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();

            // Skip patching email and password
            if("email".equalsIgnoreCase(key) || "password".equalsIgnoreCase(key)){
                System.out.println("Skipping patch for sensitive field: " + key);
                continue;
            }

            // Prepare a JSON body with only the single field to patch
            Map<String, String> singleFieldPatch = new HashMap<>();
            singleFieldPatch.put(key, value);
            String singlePatchJson = objectMapper.writeValueAsString(singleFieldPatch);

            Response response = given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer "+token)
                    .body(singlePatchJson)
                    .pathParams("id", candidateID)
                    .when()
                    .patch("/candidates/{id}")
                    .then()
                    .statusCode(200)
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("recruit/verifyResponseSchema.json"))
                    .extract().response();

            System.out.println("Response body: " + response.asString());

            // Assert that the correct ID was returned
            assertEquals(candidateID, Integer.valueOf(response.jsonPath().getInt("id")));

            // Assert only the patched field was updated correctly
            String actualValue = response.jsonPath().getString(key);
            assertEquals(value, actualValue, "Mismatch on field: " + key);
            System.out.println("Successfully patched field: " + key + " = " + value);

        }

        // Final GET request to verify the full state of the resource after all patches
        Response finalResponse = given()
                .contentType(ContentType.JSON)
                .pathParams("id", candidateID)
                .when()
                .get("/candidates/{id}")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("recruit/verifyResponseSchema.json"))
                .extract().response();

        // Merge previously updated fields with all fields patched in this test for comprehensive validation
        Map<String, String> fullyPatchedFields = new HashMap<>(updatedFields);
        fullyPatchedFields.putAll(allFieldsToPatch);

        System.out.println("Successfully patched body: " + allFieldsToPatch);

        // Assert that all expected fields match the final API response
        assertFieldsMatch(fullyPatchedFields, finalResponse);

        // this should check that email was not changed
        String updatedEmail = finalResponse.jsonPath().getString("email");
        String originalEmail = expectedFields.get("email");
        assertEquals(originalEmail, updatedEmail, "Email should NOT be updated in PATCH request");

    }

    @Test
    @Order(5)
    public void deleteCandidate(){

        Response delete = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer "+token)
                .pathParams("id", candidateID)
                .when()
                .delete("/candidates/{id}")
                .then()
                .statusCode(204)
                .extract().response();

        System.out.println("Position ID: " + candidateID + " was deleted");

        Response get = given()
                .contentType(ContentType.JSON)
                .pathParams("id", candidateID)
                .when()
                .get("/candidates/{id}")
                .then()
                .statusCode(400)
                .extract().response();

        assertEquals(400, get.getStatusCode());




    }

}
