package integration;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Client;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ShipmentService;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.junit.Assert.assertEquals;

public class ShipmentControllerIT extends BaseControllerIT {
    private Shipment shipment;
    private UUID shipmentUuid;
    private User user;
    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        shipment = testHelper.createShipment();
        shipmentUuid = shipment.getUuid();
        user = shipment.getSender().getCounterparty().getUser();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteShipment(shipment);
    }

    @Test
    public void getShipments() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("/shipments").
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getShipment() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("shipments/{uuid}", shipmentUuid.toString()).
        then().
                statusCode(SC_OK).
                body("uuid", equalTo(shipmentUuid.toString()));
    }

    @Test
    public void getShipmentForm() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("shipments/{id}/form", shipmentUuid).
        then().
                statusCode(SC_OK).
                contentType(APPLICATION_PDF_VALUE);
    }

    @Test
    public void getShipment_notFound() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("/shipments/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipment() throws Exception {
        Client sender = testHelper.createClient();
        ShipmentGroup shipmentGroup = testHelper.createShipmentGroup();
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClient().getUuid().toString());
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        String expectedJson = jsonObject.toString();

        MockMvcResponse response =
                given().
                        contentType("application/json;charset=UTF-8").
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        extract().response();
        
        String newShipmentIdString = response.path("uuid");
        String generatedBarcode = response.path("barcode");
        assertEquals(13, generatedBarcode.length());

        UUID newShipmentId = UUID.fromString(newShipmentIdString);
        
        // check created data
        Shipment createdShipment = shipmentService.getEntityByUuid(newShipmentId, sender.getCounterparty().getUser());
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipmentWithoutGroup() throws Exception {
        Client sender = testHelper.createClient();
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClient().getUuid().toString());
        String expectedJson = jsonObject.toString();

        MockMvcResponse response =
                given().
                        contentType("application/json;charset=UTF-8").
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        extract().response();

        String newShipmentIdString = response.path("uuid");
        String generatedBarcode = response.path("barcode");
        assertEquals(13, generatedBarcode.length());

        UUID newShipmentId = UUID.fromString(newShipmentIdString);

        // check created data
        Shipment createdShipment = shipmentService.getEntityByUuid(newShipmentId, sender.getCounterparty().getUser());
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipment(createdShipment);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void updateShipment() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderUuid", shipment.getSender().getUuid().toString());
        jsonObject.put("recipientUuid", shipment.getRecipient().getUuid().toString());
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                queryParam("token", user.getToken()).
                body(expectedJson).
        when().
                put("/shipments/{uuid}", shipmentUuid.toString()).
        then().
                statusCode(SC_OK);

        // check updated data
        ShipmentDto shipmentDto = shipmentMapper.toDto(shipmentService.getEntityByUuid(shipmentUuid, user));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteShipment() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                delete("/shipments/{uuid}", shipmentUuid.toString()).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteShipment_notFound() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                delete("/shipments/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
