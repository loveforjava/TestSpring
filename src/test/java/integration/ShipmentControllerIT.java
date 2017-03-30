package integration;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ShipmentService;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class ShipmentControllerIT extends BaseControllerIT {
    private Shipment shipment;
    private UUID shipmentId = null;
    private UUID anotherShipmentId = null;
    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        shipment = testHelper.createShipment();
        testHelper.populateTariffGrid();
        shipmentId = shipment.getUuid();
        anotherShipmentId = super.anotherUuid(shipmentId);
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteShipment(shipment);
    }

    @Ignore
    @Test
    public void getShipments() throws Exception {
        when().
                get("/shipments").
        then().
                statusCode(SC_OK);
    }
    
    @Ignore
    @Test
    public void getShipment() throws Exception {
        when().
                get("shipments/{id}", shipmentId.toString()).
        then().
                statusCode(SC_OK).
                body("id", equalTo(shipmentId.toString()));
    }
    
    @Ignore
    @Test
    public void getShipment_notFound() throws Exception {
        when().
                get("/shipments/{id}", anotherShipmentId.toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipment() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderId", testHelper.createClient().getUuid().toString());
        jsonObject.put("recipientId", testHelper.createClient().getUuid().toString());
        String expectedJson = jsonObject.toString();

        String newShipmentIdString =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        extract().
                        path("id");

        UUID newShipmentId = UUID.fromString(newShipmentIdString);
        
        // check created data
        Shipment createdShipment = shipmentService.getEntityById(newShipmentId);
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
        jsonObject.put("senderId", testHelper.createClient().getUuid().toString());
        jsonObject.put("recipientId", testHelper.createClient().getUuid().toString());
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
        when().
                put("/shipments/{id}", shipmentId).
        then().
                statusCode(SC_OK);

        // check updated data
        ShipmentDto shipmentDto = shipmentMapper.toDto(shipmentService.getEntityById(shipmentId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }
    
    @Ignore
    @Test
    public void deleteShipment() throws Exception {
        when().
                delete("/shipments/{id}", shipmentId).
        then().
                statusCode(SC_OK);
    }
    
    @Ignore
    @Test
    public void deleteShipment_notFound() throws Exception {
        when().
                delete("/shipments/{id}", anotherShipmentId).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
