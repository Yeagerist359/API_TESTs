package Taskboard;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetMethod {

    @BeforeEach
    public void setup() {
        baseURI = "https://taskboard.portnov.com";

    }


    @Test
    public void getTask() {
        //classic style
        Response response = get("/api/Task");

        int status = response.getStatusCode();

        assertEquals(200, status);

    }

    @Test
    public void getTaskBDD() {
        //BDD style
        given()
                .get("/api/Task")
                .then()
                .statusCode(200)
                .log()
                .all();

    }

    @Test
    public void getTaskbYId(){
        Response response = get("/api/Task/578");
        JsonPath jp = response.jsonPath();
        jp.prettyPrint();


        //Verify Task Name
        assertEquals("save word" , jp.getString("taskName"));

        //Verify description
        assertEquals("idk we need it" , jp.getString("description"));

        //Verify due date
        assertEquals("2025-06-11T01:00:06.005" , jp.getString("dueDate"));

        //Verify priority
        assertEquals(0 , jp.getInt("priority"));

        //Verify status
        assertEquals("Pending", jp.getString("status"));

        //Verify author
        assertEquals("Crazy" , jp.getString("author"));
    }
}
