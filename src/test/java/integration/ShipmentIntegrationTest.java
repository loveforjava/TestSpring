package integration;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.restassured.response.Response;

import java.util.Random;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;

public class ShipmentIntegrationTest {

    private int shipmentId = MIN_VALUE;
    private int clientId = MIN_VALUE;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        JSONObject newClient = new JSONObject();
        newClient.put("name", "created John Doe");
        newClient.put("addressId", 1);
        newClient.put("uniqueRegistrationNumber", "009");
        newClient.put("virtualPostOfficeId", 1);

        clientId = given()
                .contentType("application/json")
                .body(newClient.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .post("/clients")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");

        JSONObject newShipment = new JSONObject();
        newShipment.put("senderId", clientId);
        newShipment.put("recipientId", clientId);
        newShipment.put("deliveryType", "W2W");
        newShipment.put("weight", 5);
        newShipment.put("length", 4);
        newShipment.put("width", 0);
        newShipment.put("height", 0);
        newShipment.put("declaredPrice", 23.5);
        newShipment.put("price", 1.5);
        newShipment.put("postPay", 71);
        newShipment.put("description", null);

        int newShipmentId = given()
                .contentType("application/json")
                .body(newShipment.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .post("/shipments")
                .then()
                .body("id", greaterThan(0))
                .extract()
                .path("id");

        expect()
                .statusCode(SC_OK)
                .when()
                .get("shipments/{id}", newShipmentId);

        shipmentId = newShipmentId;
    }

    @Test
    public void getShipments() throws Exception {
        Response response = expect().statusCode(SC_OK).when().get("/shipments");
        int status = response.getStatusCode();
        assertEquals(SC_OK, status);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getShipmentById() throws Exception {
        expect().statusCode(SC_OK).when().get(String.format("shipments/%d", shipmentId)).then().body("id", equalTo(shipmentId));
    }

    @Test
    public void getShipmentById_notFound() throws Exception {
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("shipments/{id}", new Random().nextLong());
    }

    @Test
    public void getShipmentLabelForm() throws Exception {
        Response response = expect().statusCode(SC_OK).when()
                .get(String.format("/shipments/%d/label-form", shipmentId));
        String contentType = response.getHeader("Content-Type");
        int status = response.getStatusCode();
        assertEquals("application/pdf", contentType);
        assertEquals(SC_OK, status);
    }

    @Test
    public void getShipmentPostpayForm() throws Exception {
        Response response = expect()
                .statusCode(SC_OK)
                .when()
                .get(String.format("/shipments/%d/postpay-form", shipmentId));
        String contentType = response.getHeader("Content-Type");
        int status = response.getStatusCode();
        assertEquals("application/pdf", contentType);
        assertEquals(SC_OK, status);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipment() throws Exception {
        JSONObject shipment = new JSONObject();
        shipment.put("senderId", clientId);
        shipment.put("recipientId", clientId);
        shipment.put("deliveryType", "W2W");
        shipment.put("weight", 5);
        shipment.put("length", 4);
        shipment.put("width", 0);
        shipment.put("height", 0);
        shipment.put("declaredPrice", 23.5);
        shipment.put("price", 1.5);
        shipment.put("postPay", 71);
        shipment.put("description", null);

        int newShipmentId = given()
                .contentType("application/json")
                .body(shipment.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .post("/shipments")
                .then()
                .extract()
                .path("id");

        expect()
                .statusCode(SC_OK)
                .when()
                .get("shipments/{id}", newShipmentId);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateShipment() throws Exception {
        JSONObject shipment = new JSONObject();
        shipment.put("senderId", clientId);
        shipment.put("recipientId", clientId);
        shipment.put("deliveryType", "D2D");
        shipment.put("weight", 10);
        shipment.put("length", 12);
        shipment.put("width", 0);
        shipment.put("height", 0);
        shipment.put("declaredPrice", 24);
        shipment.put("price", 3.5);
        shipment.put("postPay", 84);
        shipment.put("description", null);

        given()
                .contentType("application/json")
                .body(shipment.toJSONString())
                .expect()
                .statusCode(SC_OK)
                .when()
                .put("/shipments/{id}", shipmentId)
                .then()
                .body("id", greaterThan(0));

        expect()
                .statusCode(SC_OK)
                .when()
                .get("shipments/{id}", shipmentId)
                .then()
                .body("senderId", equalTo(clientId))
                .body("recipientId", equalTo(clientId))
                .body("deliveryType", equalTo("D2D"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deleteShipment() throws Exception {
        expect()
                .statusCode(SC_OK)
                .when()
                .get("shipments/{id}", shipmentId);

        expect()
                .statusCode(SC_OK)
                .when()
                .delete("shipments/{id}", shipmentId);

        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("shipments/{id}", shipmentId);
    }

    @After
    public void tearDown() {
        try {
            expect()
                    .statusCode(SC_NOT_FOUND)
                    .when()
                    .get("shipments/{id}", shipmentId);
        } catch (AssertionError e) {
            expect()
                    .statusCode(SC_OK)
                    .when()
                    .delete("shipments/{id}", shipmentId);
        }

        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("/shipments/{id}", shipmentId);

        shipmentId = MIN_VALUE;
        clientId = MIN_VALUE;
    }
}
