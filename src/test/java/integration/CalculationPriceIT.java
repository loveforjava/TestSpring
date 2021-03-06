package integration;

import ua.ukrpost.entity.Client;
import ua.ukrpost.entity.Shipment;
import ua.ukrpost.entity.ShipmentGroup;
import ua.ukrpost.entity.User;
import ua.ukrpost.service.ShipmentService;
import ua.ukrpost.service.UserService;
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
    private User user;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        user = testHelper.createUser(testHelper.createCounterparty());
        shipmentGroup = testHelper.createShipmentGroupFor(user);
        sender = testHelper.createSenderFor(user);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length25_insideTown_noSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(18f)).
                extract().
                        path("uuid");

                Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight12_length54_insideRegion_noSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientSameRegionFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 12000);
        jsonObject.put("length", 54);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(36f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight12_length54_insideCountry_noSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientOtherRegionFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 25000);
        jsonObject.put("length", 70);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(60f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2D_weight1_length25_insideTown() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2D");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(27f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length75_insideTown() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 2500);
        jsonObject.put("length", 140);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();
        
        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(84f)).
                extract().
                        path("uuid");
        
        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);
        
        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_D2D_weight1_length25_insideTown() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "D2D");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(30f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length25_insideRegion_countryside() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientSameRegionCountrysideFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(30f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length25_insideCountry_countryside() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientOtherRegionCountrysideFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2W");
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(57f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_W2W_weight1_length25_insideCountry_declaredPriceSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientOtherRegionFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "W2W");
        jsonObject.put("declaredPrice", 1100);
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(30f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_D2W_weight1_length25_insideRegion_countryside_declaredPriceSurcharges() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        Client recipient = testHelper.createRecipientSameRegionCountrysideFor(sender.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "D2W");
        jsonObject.put("declaredPrice", 1100);
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(42f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void calcPrice_D2W_weight1_length25_insideRegion_countryside_declaredPriceSurcharges_discount()
            throws Exception {
        // create
        Client senderWithDiscount = testHelper.createSenderWithDiscount(user);

        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        jsonObject.put("sender", testHelper.toJsonWithUuid(senderWithDiscount));
        Client recipient = testHelper.createRecipientSameRegionCountrysideFor(senderWithDiscount.getCounterparty());
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("weight", 1000);
        jsonObject.put("length", 25);
        jsonObject.put("deliveryType", "D2W");
        jsonObject.put("declaredPrice", 1100);
        String expectedJson = jsonObject.toString();

        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("price", equalTo(31.71f)).
                extract().
                        path("uuid");

        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);

        // delete
        testHelper.deleteShipment(createdShipment);
    }
}
