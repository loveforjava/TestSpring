package integration;

import java.util.Random;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Integer.MIN_VALUE;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class PostOfficeControllerIntegrationTest {
    int postOfficeId = MIN_VALUE;
    int postcodePoolId = MIN_VALUE;
    
    @Before
    public void setupCase() {
        JSONObject newPostcodePool = new JSONObject();
        newPostcodePool.put("postcode", "03222");
        newPostcodePool.put("closed", false);
    
        postcodePoolId = given()
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
        JSONObject newOffice = new JSONObject();
        newOffice.put("name", "Kyiv post office");
        newOffice.put("addressId", 1);
        newOffice.put("postcodePoolId", postcodePoolId);

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

        postOfficeId = newOfficeId;
    }

    @After
    public void tearDownCase() {
        expect()
                .statusCode(SC_OK)
                .when()
                .delete("/post-offices/{id}", postOfficeId);

        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/post-offices/{id}", postOfficeId);

        postOfficeId = MIN_VALUE;
        postcodePoolId = MIN_VALUE;
    }
    
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
                .get("/post-offices/{id}", postOfficeId)
                .then()
                .body("id", equalTo(postOfficeId));
    }
    
    @Test
    public void getPostOffice_notFound() throws Exception {
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/post-offices/{id}", new Random().nextLong());
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
        JSONObject updatedOffice = new JSONObject();
        updatedOffice.put("name", "Zhytomyr post office");
        updatedOffice.put("addressId", 1);
        updatedOffice.put("postcodePoolId", postcodePoolId);
    
        String newName = given()
                .contentType("application/json;charset=UTF-8")
                .body(updatedOffice.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .put("/post-offices/{id}", postOfficeId)
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("name");
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/post-offices/{id}", postOfficeId)
                .then()
                .body("name", equalTo(newName));
    }
    
    @Test
    public void deletePostOffice() throws Exception {
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
        
        JSONObject newOffice = new JSONObject();
        newOffice.put("name", "Kharkiv post office");
        newOffice.put("addressId", 1);
        newOffice.put("postcodePoolId", newPostcodePoolId);
    
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
                .delete("/post-offices/{id}", newOfficeId);
    
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/post-offices/{id}", newOfficeId);
    }
}
