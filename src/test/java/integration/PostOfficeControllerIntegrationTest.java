package integration;

import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by Diarsid on 22.03.2017.
 */
public class PostOfficeControllerIntegrationTest {
    
    @Test
    public void getPostOffices() throws Exception {
        expect()
                .statusCode(200)
                .when()
                .get("/post-offices");
    }
    
    @Test
    public void getPostOffice() throws Exception {
        expect()
                .statusCode(200)
                .when()
                .get("/post-offices/{id}", 1)
                .then()
                .body("id", equalTo(1));
    }
    
    @Test
    public void getPostOffice_notFound() throws Exception {
        expect()
                .statusCode(404)
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
                .statusCode(200)
                .when()
                .post("/post-offices")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(200)
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
                .statusCode(200)
                .when()
                .put("/post-offices/{id}", updatedId)
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("name");
    
        expect()
                .statusCode(200)
                .when()
                .get("/post-offices/{id}", updatedId)
                .then()
                .body("name", equalTo(newName));
    }
    
    @Test
    @Ignore
    public void deletePostOffice() throws Exception {
        
        // TODO need to create PostcodePool previously
        // in order it to be eligible to be removed with
        // assigned PostOffice due to cascade REMOVE.
        
        int newPostcodePoolId = -1;
        
        JSONObject newOffice = new JSONObject();
        newOffice.put("name", "Kharkiv post office");
        newOffice.put("addressId", 1);
        newOffice.put("postcodePoolId", newPostcodePoolId);
    
        int newOfficeId = given()
                .contentType("application/json;charset=UTF-8")
                .body(newOffice.toJSONString())
                .expect()
                .statusCode(200)
                .when()
                .post("/post-offices")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(200)
                .when()
                .delete("/post-offices/{id}", newOfficeId);
    
        expect()
                .statusCode(404)
                .when()
                .get("/post-offices/{id}", newOfficeId);
    }
    
}