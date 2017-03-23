package com.opinta.controller;

import org.json.simple.JSONObject;
import org.junit.Test;

import io.restassured.response.Response;

import java.util.Random;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

public class ShipmentIntegrationTest {
    @Test
    public void getShipments() throws Exception {
        Response response = expect().statusCode(SC_OK).when().get("/shipments");
        int status = response.getStatusCode();
        assertEquals(SC_OK, status);
    }

    @Test
    public void getShipmentById() throws Exception {
        expect().statusCode(SC_OK).when().get("shipments/1").then().body("id", equalTo(1));
    }

    @Test
    public void getShipmentById_notFound() throws Exception {
        expect()
                .statusCode(SC_NOT_FOUND)
                .when()
                .get("shipments/{id}", new Random().nextInt() + 1000);
    }

    @Test
    public void getShipmentLabelForm() throws Exception {
        Response response = expect().statusCode(SC_OK).when().get("/shipments/1/label-form");
        String contentType = response.getHeader("Content-Type");
        int status = response.getStatusCode();
        assertEquals("application/pdf", contentType);
        assertEquals(SC_OK, status);
    }

    @Test
    public void getShipmentPostpayForm() throws Exception {
        Response response = expect().statusCode(SC_OK).when().get("/shipments/1/postpay-form");
        String contentType = response.getHeader("Content-Type");
        int status = response.getStatusCode();
        assertEquals("application/pdf", contentType);
        assertEquals(SC_OK, status);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipment() throws Exception {
        JSONObject shipment = new JSONObject();
        shipment.put("senderId", 2);
        shipment.put("recipientId", 2);
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
        shipment.put("senderId", 1);
        shipment.put("recipientId", 2);
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
                .put("/shipments/{id}", 1)
                .then()
                .body("id", greaterThan(0));

        expect()
                .statusCode(SC_OK)
                .when()
                .get("shipments/{id}", 1)
                .then()
                .body("senderId", equalTo(1))
                .body("recipientId", equalTo(2))
                .body("deliveryType", equalTo("D2D"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void deleteShipment() throws Exception {
        try {
            expect()
                    .statusCode(SC_OK)
                    .when()
                    .delete("shipments/{id}", 1);
        } catch (AssertionError e) {
            JSONObject shipment = new JSONObject();
            shipment.put("senderId", 1);
            shipment.put("recipientId", 2);
            shipment.put("deliveryType", "D2D");
            shipment.put("weight", 10);
            shipment.put("length", 12);
            shipment.put("width", 0);
            shipment.put("height", 0);
            shipment.put("declaredPrice", 24);
            shipment.put("price", 3.5);
            shipment.put("postPay", 84);
            shipment.put("description", null);

            int newShipmentId = given()
                    .contentType("application/json")
                    .body(shipment.toJSONString())
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
                    .delete("shipments/{id}", newShipmentId);

            expect()
                    .statusCode(SC_NOT_FOUND)
                    .when()
                    .get("shipments/{id}", newShipmentId);
        }
    }
}