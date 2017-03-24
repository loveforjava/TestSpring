package integration;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Integer.MIN_VALUE;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class VirtualPostOfficeControllerIntegrationTest {
    int virtualPostOfficeId = MIN_VALUE;
    int postcodePoolId = MIN_VALUE;
    
    @Before
    public void setUp() {
        JSONObject newPostcodePool = new JSONObject();
        newPostcodePool.put("postcode", "03222");
        newPostcodePool.put("closed", false);
    
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
    
        JSONObject newVirtualPostOffice = new JSONObject();
        newVirtualPostOffice.put("name", "Office to be removed");
        newVirtualPostOffice.put("activePostcodePoolId", newPostcodePoolId);
        newVirtualPostOffice.put("description", "some text");
    
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
        
        virtualPostOfficeId = newId;
        postcodePoolId = newPostcodePoolId;
    }
    
    @After
    public void tearDown() {
        delete("/virtual-post-offices/{id}", virtualPostOfficeId);
    }
    
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
    
        delete("/virtual-post-offices/{id}", newId);
    }
    
    @Test
    public void getPostOfficeById() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/virtual-post-offices/{id}", 1);
    }
    
    @Test
    public void getClientsOfVirtualPostOffice() {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/virtual-post-offices/{id}/clients", 1);
    }
    
    @Test
    public void getPostOfficeById_notFound() throws Exception {
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/virtual-post-offices/{id}", virtualPostOfficeId + 1);
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
                .put("/virtual-post-offices/{id}", virtualPostOfficeId)
                .then()
                .body("id", equalTo(virtualPostOfficeId))
                .extract()
                .path("id");
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/virtual-post-offices/{id}", virtualPostOfficeId)
                .then()
                .body("name", equalTo("new name"))
                .body("description", equalTo("new info"));
    }
    
    @Test
    public void deletePostOfficeById() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .delete("/virtual-post-offices/{id}", virtualPostOfficeId);
    
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/virtual-post-offices/{id}", virtualPostOfficeId);
    }
}
