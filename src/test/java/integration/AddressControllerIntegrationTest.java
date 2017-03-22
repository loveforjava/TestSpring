package integration;

import org.json.simple.JSONObject;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by Diarsid on 22.03.2017.
 */
public class AddressControllerIntegrationTest {
    
    @Test
    public void getAddresses() throws Exception {
        expect()
                .statusCode(200)
                .when()
                .get("/addresses");
    }
    
    @Test
    public void getAddress() throws Exception {
        expect()
                .statusCode(200)
                .when()
                .get("/addresses/{id}", 1)
                .then()
                .body("id", equalTo(1));
    }
    
    @Test
    public void getAddress_notFound() throws Exception {
        expect()
                .statusCode(404)
                .when()
                .get("/addresses/{id}", 764563);
    }
    
    @Test
    public void createAddress() throws Exception {
        JSONObject newAddr = new JSONObject();
        newAddr.put("postcode", "020991");
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
                .statusCode(200)
                .when()
                .post("/addresses")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(200)
                .when()
                .get("addresses/{id}", newAddrId);
    }
    
    @Test
    public void updateAddress() throws Exception {
        
        int updatedId = 1;
        
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
                .statusCode(200)
                .when()
                .put("/addresses/{id}", updatedId)
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("district");
    
        expect()
                .statusCode(200)
                .when()
                .get("/addresses/{id}", updatedId)
                .then()
                .body("district", equalTo(newDistrict));
    }
    
    @Test
    public void deleteAddress() throws Exception {
        JSONObject newAddr = new JSONObject();
        newAddr.put("postcode", "020991");
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
                .statusCode(200)
                .when()
                .post("/addresses")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(200)
                .when()
                .delete("/addresses/{id}", newAddrId);
    
        expect()
                .statusCode(404)
                .when()
                .get("/addresses/{id}", newAddrId);
    }
    
}