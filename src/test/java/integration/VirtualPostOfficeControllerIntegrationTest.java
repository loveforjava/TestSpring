package integration;

import java.util.Random;

import org.json.simple.JSONObject;
import org.junit.Test;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.fail;

public class VirtualPostOfficeControllerIntegrationTest {
    @Test
    public void getAllPostOffices() throws Exception {
        expect()
                .statusCode(SC_OK)
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
                .statusCode(SC_OK)
                .when()
                .post("/virtual-post-offices/")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/virtual-post-offices/{id}", newId);
    }
    
    @Test
    public void getPostOfficeById() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/virtual-post-offices/{id}", 1);
    }
    
    @Test
    public void getPostOfficeById_notFound() throws Exception {
        expect()
                .statusCode(SC_NOT_FOUND)
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
                .statusCode(SC_OK)
                .when()
                .put("/virtual-post-offices/{id}", 1)
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(SC_OK)
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
                    .statusCode(SC_OK)
                    .when()
                    .delete("/virtual-post-offices/{id}", 1);
            fail();
        } catch (AssertionError e) {
    
            // create PostcodePool
            JSONObject newPostcodePool = new JSONObject();
            newPostcodePool.put("postcode", "03222");
            newPostcodePool.put("closed", false);
    
            // save PostcodePool and get its real database id
            int newPostcodePoolId = given()
                    .contentType("application/json;charset=UTF-8")
                    .body(newPostcodePool.toJSONString())
                    .expect()
                    .statusCode(SC_OK)
                    .when()
                    .post("/postcodes")
                    .then()
                    .body("id", greaterThan(0))
                    .extract()
                    .path("id");
    
            // create new office using previously created
            // real postcode pool id
            JSONObject newVirtualPostOffice = new JSONObject();
            newVirtualPostOffice.put("name", "Office to be removed");
            newVirtualPostOffice.put("activePostcodePoolId", newPostcodePoolId);
            newVirtualPostOffice.put("description", "some text");
    
            // saving office and get its real id
            int newId = given()
                    .contentType("application/json;charset=UTF-8")
                    .body(newVirtualPostOffice.toJSONString())
                    .expect()
                    .statusCode(SC_OK)
                    .when()
                    .post("/virtual-post-offices/")
                    .then()
                    .body("id", greaterThan(0))
                    .extract()
                    .path("id");
            
            // remove office by real id
            expect()
                    .statusCode(SC_OK)
                    .when()
                    .delete("/virtual-post-offices/{id}", newId);
    
            // assert that office have been removed
            expect()
                    .statusCode(SC_NOT_FOUND)
                    .when()
                    .get("/virtual-post-offices/{id}", newId);
        }
    }
}