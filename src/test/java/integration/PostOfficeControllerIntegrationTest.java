package integration;

import org.json.simple.JSONObject;
import org.junit.Test;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class PostOfficeControllerIntegrationTest {
    @Test
    public void getPostOffices() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/post-offices");
    }
    
    @Test
    public void getPostOffice() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/post-offices/{id}", 1)
                .then()
                .body("id", equalTo(1));
    }
    
    @Test
    public void getPostOffice_notFound() throws Exception {
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/post-offices/{id}", 764563);
    }
    
    @Test
    public void createPostOffice() throws Exception {
        JSONObject newOffice = new JSONObject();
        newOffice.put("name", "Kyiv post office");
        newOffice.put("addressId", 1);
        newOffice.put("postcodePoolId", 2);
    
        int newOfficeId = given()
                .contentType("application/json;charset=UTF-8")
                .body(newOffice.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .post("/post-offices")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("post-offices/{id}", newOfficeId);
    }
    
    @Test
    public void updatePostOffice() throws Exception {
        int updatedId = 1;
    
        JSONObject updatedOffice = new JSONObject();
        updatedOffice.put("name", "Zhytomyr post office");
        updatedOffice.put("addressId", 1);
        updatedOffice.put("postcodePoolId", 2);
    
        String newName = given()
                .contentType("application/json;charset=UTF-8")
                .body(updatedOffice.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .put("/post-offices/{id}", updatedId)
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("name");
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/post-offices/{id}", updatedId)
                .then()
                .body("name", equalTo(newName));
    }
    
    @Test
    public void deletePostOffice() throws Exception {
        
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
        JSONObject newOffice = new JSONObject();
        newOffice.put("name", "Kharkiv post office");
        newOffice.put("addressId", 1);
        newOffice.put("postcodePoolId", newPostcodePoolId);
    
        // saving office and get its real id
        int newOfficeId = given()
                .contentType("application/json;charset=UTF-8")
                .body(newOffice.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .post("/post-offices")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        // remove office by real id
        expect()
                .statusCode(SC_OK)
                .when()
                .delete("/post-offices/{id}", newOfficeId);
    
        // assert that office have been removed
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/post-offices/{id}", newOfficeId);
    }
    
}