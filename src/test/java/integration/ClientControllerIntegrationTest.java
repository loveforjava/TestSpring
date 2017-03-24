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

public class ClientControllerIntegrationTest {
    int clientId = MIN_VALUE;
    int clientAddressId = MIN_VALUE;
    
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
    
        clientAddressId = given()
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
    
        JSONObject newClient = new JSONObject();
        newClient.put("name", "created John Doe");
        newClient.put("addressId", 1);
        newClient.put("uniqueRegistrationNumber", "009");
        newClient.put("virtualPostOfficeId", 1);
    
        clientId = given()
                .contentType("application/json;charset=UTF-8")
                .body(newClient.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .post("/clients")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    }
    
    @After
    public void tearDown() {
        delete("clients/{id}", clientId);
    }
    
    @Test
    public void getAllClients() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("clients");
    }
    
    @Test
    public void getClientById() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("clients/{id}", clientId);
    }
    
    @Test
    public void getClientById_notFound() throws Exception {
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("clients/{id}", clientId + 1);
    }
    
    @Test
    public void getShipmentsByClientId() {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("clients/{id}/shipments", clientId);
    }
    
    @Test
    public void createClient() throws Exception {
        JSONObject newClient = new JSONObject();
        newClient.put("name", "created John Doe");
        newClient.put("addressId", 1);
        newClient.put("uniqueRegistrationNumber", "009");
        newClient.put("virtualPostOfficeId", 1);
    
        int newClientId = given()
                .contentType("application/json;charset=UTF-8")
                .body(newClient.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .post("/clients")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("clients/{id}", newClientId);
    }
    
    @Test
    public void updateClient() throws Exception {
        JSONObject updatedClient = new JSONObject();
        updatedClient.put("name", "updated New client name");
        updatedClient.put("addressId", clientAddressId);
        updatedClient.put("uniqueRegistrationNumber", "009");
        updatedClient.put("virtualPostOfficeId", 1);
        
        given()
                .contentType("application/json;charset=UTF-8")
                .body(updatedClient.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .put("/clients/{id}", clientId)
                .then()
                .body("id", equalTo(clientId));
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("clients/{id}", clientId)
                .then()
                .body("id", equalTo(clientId))
                .body("name", equalTo("updated New client name"));
    }
    
    @Test
    public void deleteClient() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .delete("clients/{id}", clientId);
    
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("clients/{id}", clientId);
    }
}
