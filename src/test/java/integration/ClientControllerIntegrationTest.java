package integration;

import java.util.Random;

import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.Test;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;

public class ClientControllerIntegrationTest {
    @Test
    public void getAllClients() throws Exception {
        Response response = expect().statusCode(SC_OK).when().get("/clients");
        int status = response.getStatusCode();
        assertEquals(SC_OK, status);
    }
    
    @Test
    public void getClientById() throws Exception {
        expect().statusCode(SC_OK).when().get("clients/1").then().body("id", equalTo(1));
        expect().statusCode(SC_OK).when().get("clients/2").then().body("id", equalTo(2));
    }
    
    @Test
    public void getClientById_notFound() throws Exception {
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("clients/{id}", new Random().nextInt());
    }
    
    @Test
    public void getShipmentsByClientId() {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("clients/{id}/shipments", 1);
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
        updatedClient.put("addressId", 1);
        updatedClient.put("uniqueRegistrationNumber", "009");
        updatedClient.put("virtualPostOfficeId", 1);
        
        given()
                .contentType("application/json;charset=UTF-8")
                .body(updatedClient.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .put("/clients/{id}", 1).then().body("id", greaterThan(0));
    
        expect()
                .statusCode(SC_OK)
                .when()
                .get("clients/{id}", 1)
                .then()
                .body("id", equalTo(1))
                .body("name", equalTo("updated New client name"));
    }
    
    @Test
    public void deleteClient() throws Exception {
        try {
            expect()
                    .statusCode(SC_OK)
                    .when()
                    .delete("clients/{id}", 1);
        } catch (AssertionError e) {
            JSONObject newClient = new JSONObject();
            newClient.put("name", "John Doe");
            newClient.put("addressId", 1);
            newClient.put("uniqueRegistrationNumber", "123");
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
                    .delete("clients/{id}", newClientId);
    
            expect()
                    .statusCode(SC_NOT_FOUND)
                    .when()
                    .get("clients/{id}", newClientId);
        }
    }
}