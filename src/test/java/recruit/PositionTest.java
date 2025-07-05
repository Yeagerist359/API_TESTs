package recruit;

import base.BaseTest;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.*;


import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PositionTest extends BaseTest {

    // generate token for each test
    @BeforeEach
    public void setupToken() {
        token = generateToken();
    }


    // List of reusable position field names for extracting and verifying JSON body
//    public static final List<String> POSITIONS_FIELDS = List.of("title", "address", "city", "state", "zip", "description", "company");

    private static Integer positionID;
    private static String token;
    private static Map<String,String> expectedFields = new HashMap<>();
    private static Map<String,String> updatedFields = new HashMap<>();

    // Helper method to compare expected field values against API response
    private void assertFieldsMatch(Map<String, String> expected , Response response){
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            assertEquals(entry.getValue(), response.jsonPath().getString(entry.getKey()), "Mismatch on field: " + entry.getKey());
        }
    }

    @Test
    // 1st test to create position and storing positionID and expectedFields of a body
    @Order(1)
    public void createPosition(){
        String posBody = readFromJsonFile("src/test/resources/recruit/createPosition.json");

        Response response = createNewPosition(posBody, token);
        assertEquals(201, response.getStatusCode());
        assertTrue(response.getHeader("Content-Type").contains("application/json"), "Expected JSON content type");
        positionID = response.jsonPath().getInt("id");

        // Extract and store all response fields as expectedFields
        for (String field : POSITIONS_FIELDS){
            expectedFields.put(field, response.jsonPath().getString(field));
        }

        System.out.println("Created Position ID: " + positionID);

    }

    @Test
    // 2nd test to read created position
    @Order(2)
    public void readPosition(){
        Response response = given()
                .contentType(ContentType.JSON)
                .pathParams("id", positionID)
                .when()
                .get("/positions/{id}")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("recruit/verifyResponseSchema.json"))
                .extract().response();

        assertEquals(positionID, Integer.valueOf(response.jsonPath().getInt("id")));
        assertTrue(response.getHeader("Content-Type").contains("application/json"), "Expected JSON content type");

        //comparing "expectedFields" to a response that we got from get request
        assertFieldsMatch(expectedFields, response);

    }

    @Test
    //3rd test updating created position with new json file
    @Order(3)
    public void updatePosition(){
        String updateBody = readFromJsonFile("src/test/resources/recruit/putPosition.json");

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer "+token)
                .body(updateBody)
                .pathParams("id", positionID)
                .when()
                .put("/positions/{id}")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("recruit/verifyResponseSchema.json"))
                .extract().response();

        assertEquals(positionID, Integer.valueOf(response.jsonPath().getInt("id")));
        System.out.println("Updated Position ID: " + positionID);
        assertTrue(response.getHeader("Content-Type").contains("application/json"), "Expected JSON content type");

        //making new body into "updatedFields"
        for (String newField : POSITIONS_FIELDS){
            String value = response.jsonPath().getString(newField);
            updatedFields.put(newField, value);
        }

        // Verifying updated response fields match the expected updated values
        assertFieldsMatch(updatedFields, response);

    }


    @Test
    @Order(4)
    public void patchPosition() throws IOException {
        String patchBody = readFromJsonFile("src/test/resources/recruit/patchPosition.json");

        // Convert JSON into a Map of field:value pairs using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> allFieldsToPatch = objectMapper.readValue(patchBody, new TypeReference<Map<String, String>>() {});

        // Iterate through each field to patch one at a time
        for (Map.Entry<String, String> entry : allFieldsToPatch.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();

            // Build single-field patch body as JSON
            Map<String,String> singleFieldPatch = new HashMap<>();
            singleFieldPatch.put(key,value);
            String singlePatchJson = objectMapper.writeValueAsString(singleFieldPatch);

            // Send PATCH request for the single field
            Response response = given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer "+token)
                    .body(singlePatchJson)
                    .pathParams("id", positionID)
                    .when()
                    .patch("/positions/{id}")
                    .then()
                    .statusCode(200)
                    .extract().response();
            System.out.println("Response body: " + response.asString());

            // Assert that the correct ID was returned
            assertEquals(positionID, Integer.valueOf(response.jsonPath().getInt("id")));

            // Assert only the patched field was updated correctly
            String actualValue = response.jsonPath().getString(key);
            assertEquals(value, actualValue, "Mismatch on field: " + key);

            System.out.println("Successfully patched field: " + key + " = " + value);


        }

        // Final GET request to verify the full state of the resource after all patches
        Response finalResponse = given()
                .contentType(ContentType.JSON)
                .pathParams("id", positionID)
                .when()
                .get("/positions/{id}")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("recruit/verifyResponseSchema.json"))
                .extract().response();

        Map<String, String> fullyPatchedFields = new HashMap<>(updatedFields);
        fullyPatchedFields.putAll(allFieldsToPatch);

        System.out.println("Successfully patched body: " + allFieldsToPatch);

        assertFieldsMatch(fullyPatchedFields, finalResponse);



    }



    @Test
    // test to delete created position
    @Order(5)
    public void deletePosition(){
        token = generateToken();

        Response delete = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer "+token)
                .pathParams("id", positionID)
                .when()
                .delete("/positions/{id}")
                .then()
                .statusCode(204)
                .extract().response();

        System.out.println("Position ID: " + positionID + " was deleted");

        Response get = given()
                .contentType(ContentType.JSON)
                .pathParams("id", positionID)
                .when()
                .get("/positions/{id}")
                .then()
                .statusCode(400)
                .extract().response();

        assertEquals(400, get.getStatusCode());


    }

}
