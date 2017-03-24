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

public class AddressControllerIntegrationTest {
    int addressId = MIN_VALUE;
    
    @Before
    public void setUp() {
        JSONObject newAddr = new JSONObject();
        newAddr.put("postcode", "02099");
        newAddr.put("region", "Kyiv");
        newAddr.put("district", "Darnitskyi");
        newAddr.put("city", "Kyiv");
        newAddr.put("street", "Yaltinskaya");
        newAddr.put("houseNumber", "51");
        newAddr.put("appartmentNumber", "32");
        newAddr.put("description", "none");
    
        addressId = given()
                .contentType("application/json;charset=UTF-8")
                .body(newAddr.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .post("/addresses")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    }
    
    @After
    public void tearDown() {
        delete("/addresses/{id}", addressId);
    }
    
    @Test
    public void getAddresses() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/addresses");
    }
    
    @Test
    public void getAddress() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/addresses/{id}", 1)
                .then()
                .body("id", equalTo(1));
    }
    
    @Test
    public void getAddress_notFound() throws Exception {
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/addresses/{id}", addressId + 1);
    }
    
    @Test
    public void createAddress() throws Exception {
        JSONObject newAddr = new JSONObject();
        newAddr.put("postcode", "02099");
        newAddr.put("region", "Kyiv");
        newAddr.put("district", "Darnitskyi");
        newAddr.put("city", "Kyiv");
        newAddr.put("street", "Yaltinskaya");
        newAddr.put("houseNumber", "51");
        newAddr.put("appartmentNumber", "32");
        newAddr.put("description", "none");
    
        int newAddrId = given()
                .contentType("application/json;charset=UTF-8")
                .body(newAddr.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .post("/addresses")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("addresses/{id}", newAddrId);
    }
    
    @Test
    public void updateAddress() throws Exception {
        JSONObject updatedAddr = new JSONObject();
        updatedAddr.put("postcode", "020991");
        updatedAddr.put("region", "Kyiv");
        updatedAddr.put("district", "Darnitskyi");
        updatedAddr.put("city", "Kyiv");
        updatedAddr.put("street", "Yaltinskaya");
        updatedAddr.put("houseNumber", "51");
        updatedAddr.put("appartmentNumber", "32");
        updatedAddr.put("description", "none");
    
        String newDistrict = given()
                .contentType("application/json;charset=UTF-8")
                .body(updatedAddr.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .put("/addresses/{id}", addressId)
                .then()
                .body("id", equalTo(addressId))
                .extract()
                .path("district");
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("/addresses/{id}", addressId)
                .then()
                .body("district", equalTo(newDistrict));
    }
    
    @Test
    public void deleteAddress() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .delete("/addresses/{id}", addressId);
    
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/addresses/{id}", addressId);
    }
}
