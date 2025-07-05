package Taskboard;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class PostMethod {

    @BeforeEach
    public void setup() {
        baseURI = "https://taskboard.portnov.com";

    }


    @Test
    public void postTask(){

        String taskName = "save word";
        String description = "idk we need it";
        String dueDate = "2025-06-11T01:00:06.005Z";
        Integer priority = 0;
        String status = "Pending";
        String author = "Crazy";

        JSONObject jsonObject = new JSONObject();
        //puts data into this map
        jsonObject.put("id",0);
        jsonObject.put("taskName",taskName);
        jsonObject.put("description",description);
        jsonObject.put("dueDate",dueDate);
        jsonObject.put("priority",priority);
        jsonObject.put("status",status);
        jsonObject.put("author",author);

        String stringBody = jsonObject.toString();

        //Store the Response into an Object
        Response response = given()
                .contentType(ContentType.JSON)
                .body(stringBody)
                .when()
                .post("/api/task");
        JsonPath jp = response.jsonPath();

        response.prettyPrint();

        //Assert all the data in the response
        assertEquals(201 , response.getStatusCode());

        //Verify id
        //assertFalse(jp.getInt("id"), );

        //Verify Task Name
        assertEquals(taskName , jp.getString("taskName"));

        //Verify description
        assertEquals(description , jp.getString("description"));

        //Verify due date
        assertEquals(dueDate , jp.getString("dueDate"));

        //Verify priority
        assertEquals(priority , jp.getInt("priority"));

        //Verify status
        assertEquals(status, jp.getString("status"));

        //Verify author
        assertEquals(author , jp.getString("author"));





    }

}
