package integration;

import java.util.Random;

import org.json.simple.JSONObject;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.fail;

/**
 * Created by Diarsid on 22.03.2017.
 */
public class VirtualPostOfficeControllerIntegrationTest {
    
    @Test
    public void getAllPostOffices() throws Exception {
        expect()
                .statusCode(200)
                .when()
                .get("/clients");
    }
    
    @Test
    public void createVirtualPostOffice() throws Exception {
        JSONObject newVirtualPostOffice = new JSONObject();
        newVirtualPostOffice.put("name", "Rozetka");
        newVirtualPostOffice.put("activePostcodePoolId", 1);
        newVirtualPostOffice.put("description", "Rozetka LTD");
        
        int newId = given()
                .contentType("application/json;charset=UTF-8")
                .body(newVirtualPostOffice.toJSONString())
                .expect()
                .statusCode(200)
                .when()
                .post("/virtual-post-offices/")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(200)
                .when()
                .get("/virtual-post-offices/{id}", newId);
    }
    
    @Test
    public void getPostOfficeById() throws Exception {
        expect()
                .statusCode(200)
                .when()
                .get("/virtual-post-offices/{id}", 1);
    }
    
    @Test
    public void getPostOfficeById_notFound() throws Exception {
        expect()
                .statusCode(404)
                .when()
                .get("/virtual-post-offices/{id}", new Random().nextInt());
    }
    
    @Test
    public void updatePostOfficeById() throws Exception {
        JSONObject updatedVirtualPostOffice = new JSONObject();
        updatedVirtualPostOffice.put("name", "new name");
        updatedVirtualPostOffice.put("activePostcodePoolId", 3);
        updatedVirtualPostOffice.put("description", "new info");
    
        given()
                .contentType("application/json;charset=UTF-8")
                .body(updatedVirtualPostOffice.toJSONString())
                .expect()
                .statusCode(200)
                .when()
                .put("/virtual-post-offices/{id}", 1)
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(200)
                .when()
                .get("/virtual-post-offices/{id}", 1)
                .then()
                .body("name", equalTo("new name"))
                .body("description", equalTo("new info"));
    }
    
    @Test
    public void deletePostOfficeById() throws Exception {
        try {
            expect()
                    .statusCode(200)
                    .when()
                    .delete("/virtual-post-offices/{id}", 1);
            fail();
        } catch (AssertionError e) {
            // is OK.
        }
    }
    
}