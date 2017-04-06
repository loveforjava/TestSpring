package integration;

import com.opinta.entity.Client;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.service.ShipmentService;
import integration.helper.TestHelper;
import java.util.UUID;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CalculationPriceIT extends BaseControllerIT {
    private Client sender;
    private ShipmentGroup shipmentGroup;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        sender = testHelper.createClientAsSender();
        shipmentGroup = testHelper.createShipmentGroup();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length25_insideTown_noSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClient().getUuid().toString());
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(18f)).
                extract().
                        path("uuid");

                Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                        sender.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight12_length54_insideRegion_noSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClientSameRegion().getUuid().toString());
        jsonObject.put("weight", 12000);
        jsonObject.put("length", 54);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(36f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                sender.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight12_length54_insideCountry_noSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClientOtherRegion().getUuid().toString());
        jsonObject.put("weight", 25000);
        jsonObject.put("length", 70);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(60f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                sender.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2D_weight1_length25_insideTown() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClient().getUuid().toString());
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2D");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(27f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                sender.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length75_insideTown() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClient().getUuid().toString());
        jsonObject.put("weight", 2500);
        jsonObject.put("length", 140);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();
        
        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(84f)).
                extract().
                        path("uuid");
        
        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                sender.getCounterparty().getUser());
        
        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_D2D_weight1_length25_insideTown() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClient().getUuid().toString());
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "D2D");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(30f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                sender.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length25_insideRegion_countryside() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClientSameRegionCountryside().getUuid().toString());
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(30f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                sender.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length25_insideCountry_countryside() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClientOtherRegionCountryside().getUuid().toString());
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(57f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                sender.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length25_insideCountry_declaredPriceSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClientOtherRegion().getUuid().toString());
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2W");
        jsonObject.put("declaredPrice", 1100);
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(30f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                sender.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_D2W_weight1_length25_insideRegion_countryside_declaredPriceSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", sender.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClientSameRegionCountryside().getUuid().toString());
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "D2W");
        jsonObject.put("declaredPrice", 1100);
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", sender.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(42f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                sender.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_D2W_weight1_length25_insideRegion_countryside_declaredPriceSurcharges_discount()
            throws Exception {
        Client clientWithDiscount = testHelper.createClientWithDiscount();
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("senderUuid", clientWithDiscount.getUuid().toString());
        jsonObject.put("recipientUuid", testHelper.createClientSameRegionCountryside().getUuid().toString());
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "D2W");
        jsonObject.put("declaredPrice", 1100);
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", clientWithDiscount.getCounterparty().getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(37.8f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid),
                clientWithDiscount.getCounterparty().getUser());

        // delete
        testHelper.deleteShipment(createdShipment);
    }
}
