package integration;

import com.opinta.constraint.RegexMatcher;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ShipmentService;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static com.opinta.constraint.RegexPattern.BARCODE_REGEX;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

public class ShipmentControllerIT extends BaseControllerIT {
    private Shipment shipment;
    private UUID shipmentUuid;
    private User user;
    private Counterparty counterparty;
    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        counterparty = testHelper.createCounterparty();
        user = testHelper.createUser(counterparty);
        shipment = testHelper.createShipmentFor(counterparty);
        shipmentUuid = shipment.getUuid();
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
                get("shipments/{uuid}/form", shipmentUuid).
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
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipment_presavedSenderAndRecipient() throws Exception {
        user = testHelper.createUser(testHelper.createCounterparty());
        Client sender = testHelper.createSenderFor(user);
        Client recipient = testHelper.createRecipientFor(user);
        ShipmentGroup shipmentGroup = testHelper.createShipmentGroupFor(user);
        
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        // populate input json with sender and recipient uuid
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
        String expectedJson = jsonObject.toString();

        long timeStarted = System.currentTimeMillis();
        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("barcode", RegexMatcher.matches(BARCODE_REGEX)).
                extract().
                        path("uuid");
        long timeFinished = System.currentTimeMillis();

        // check created data
        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);
        long timeCreated = createdShipment.getCreated().getTime();
        long timeModified = createdShipment.getLastModified().getTime();

        assertTrue("Shipment has wrong created time", (timeFinished > timeCreated && timeCreated > timeStarted));
        assertTrue("Shipment has wrong modified time", (timeFinished > timeModified && timeModified > timeStarted));
        assertNotNull("Shipment doesn't have a creator", createdShipment.getCreator());
        assertNotNull("Shipment doesn't have a modifier on creation!", createdShipment.getLastModifier());
        assertThat("Shipment was created with wrong user!",
                createdShipment.getCreator().getToken(), equalTo(user.getToken()));
        assertThat("Shipment was created with wrong modifier!",
                createdShipment.getLastModifier().getToken(), equalTo(user.getToken()));

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));

        assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipment_presavedSenderAndRecipient_senderByPostId() throws Exception {
        Client sender = testHelper.createSenderFor(user);
        testHelper.assignPostIdTo(sender);
        Client recipient = testHelper.createRecipientFor(user);
        ShipmentGroup shipmentGroup = testHelper.createShipmentGroupFor(user);

        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        // populate input json with sender and recipient uuid
        jsonObject.put("sender", testHelper.toJsonWithPostId(sender));
        jsonObject.put("recipient", testHelper.toJsonWithUuid(recipient));
        jsonObject.put("shipmentGroupUuid", shipmentGroup.getUuid().toString());
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
                        body("barcode", RegexMatcher.matches(BARCODE_REGEX)).
                extract().
                        path("uuid");

        // check created data
        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));

        assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipmentWithoutGroup_unsavedSenderAndRecipient() throws Exception {
        Counterparty counterparty = testHelper.createCounterparty();
        user = testHelper.createUser(counterparty);
        Address senderAddress = testHelper.createAddress();
        Address recipientAddress = testHelper.createAddressOtherRegion();
        
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        // populate json object template with clients data
        testHelper.adjustClientData((JSONObject) jsonObject.get("sender"), senderAddress);
        testHelper.adjustClientData((JSONObject) jsonObject.get("recipient"), recipientAddress);
        String inputJson = jsonObject.toJSONString();

        long timeStarted = System.currentTimeMillis();
        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(inputJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("barcode", RegexMatcher.matches(BARCODE_REGEX)).
                extract().
                        path("uuid");
        long timeFinished = System.currentTimeMillis();

        
        // check created data
        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);
        long timeCreated = createdShipment.getCreated().getTime();
        long timeModified = createdShipment.getLastModified().getTime();
        assertTrue("Shipment has wrong created time", (timeFinished > timeCreated && timeCreated > timeStarted));
        assertTrue("Shipment has wrong modified time", (timeFinished > timeModified && timeModified > timeStarted));
        assertNotNull("Shipment doesn't have a creator", createdShipment.getCreator());
        assertNotNull("Shipment doesn't have a modifier on creation!", createdShipment.getLastModifier());
        assertThat("Shipment was created with wrong user!",
                createdShipment.getCreator().getToken(), equalTo(user.getToken()));
        assertThat("Shipment was created with wrong modifier!",
                createdShipment.getLastModifier().getToken(), equalTo(user.getToken()));
        // adjust jsonObject with actual data, modified in server
        testHelper.mergeClientNames((JSONObject) jsonObject.get("sender"), createdShipment.getSender());
        testHelper.mergeClientNames((JSONObject) jsonObject.get("recipient"), createdShipment.getRecipient());
                
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));
        
        String expectedJson = jsonObject.toJSONString();
        assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipment(createdShipment);
    }
    
    
    @Test
    @SuppressWarnings("unchecked")
    public void createShipmentWithoutGroup_savedSender_unsavedRecipient() throws Exception {
        user = testHelper.createUser(testHelper.createCounterparty());
        Client sender = testHelper.createSenderFor(user);
        Address recipientAddress = testHelper.createAddressOtherRegion();
        
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("sender", testHelper.toJsonWithUuid(sender));
        testHelper.adjustClientData((JSONObject) jsonObject.get("recipient"), recipientAddress);
        String inputJson = jsonObject.toJSONString();

        long timeStarted = System.currentTimeMillis();
        String newShipmentUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(inputJson).
                when().
                        post("/shipments").
                then().
                        statusCode(SC_OK).
                        body("barcode", RegexMatcher.matches(BARCODE_REGEX)).
                extract().
                        path("uuid");
        long timeFinished = System.currentTimeMillis();

        
        // check created data
        Shipment createdShipment = shipmentService.getEntityByUuid(UUID.fromString(newShipmentUuid), user);
        long timeCreated = createdShipment.getCreated().getTime();
        long timeModified = createdShipment.getLastModified().getTime();

        assertTrue("Shipment has wrong created time", (timeFinished > timeCreated && timeCreated > timeStarted));
        assertTrue("Shipment has wrong modified time", (timeFinished > timeModified && timeModified > timeStarted));
        assertNotNull("Shipment doesn't have a creator", createdShipment.getCreator());
        assertNotNull("Shipment doesn't have a modifier on creation!", createdShipment.getLastModifier());
        assertThat("Shipment was created with wrong user!",
                createdShipment.getCreator().getToken(), equalTo(user.getToken()));
        assertThat("Shipment was created with wrong modifier!",
                createdShipment.getLastModifier().getToken(), equalTo(user.getToken()));
        // adjust jsonObject with actual data, modified into
        testHelper.mergeClientNames((JSONObject) jsonObject.get("sender"), createdShipment.getSender());
        testHelper.mergeClientNames((JSONObject) jsonObject.get("recipient"), createdShipment.getRecipient());
        
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));
        
        String expectedJson = jsonObject.toJSONString();
        assertEquals(expectedJson, actualJson, false);
        
        // delete
        testHelper.deleteShipment(createdShipment);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void updateShipment() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("sender", testHelper.toJsonWithUuid(shipment.getSender()));
        jsonObject.put("recipient", testHelper.toJsonWithUuid(shipment.getRecipient()));
        String expectedJson = jsonObject.toString();
        ShipmentDto shipmentDtoBeforeUpdate = shipmentMapper.toDto(shipmentService.getEntityByUuid(shipmentUuid, user));

        long timeStarted = System.currentTimeMillis();
        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(expectedJson).
        when().
                put("/shipments/{uuid}", shipmentUuid.toString()).
        then().
                body("barcode", equalTo(shipmentDtoBeforeUpdate.getBarcode())).
                statusCode(SC_OK);
        long timeFinished = System.currentTimeMillis();

        // check updated data
        Shipment updatedShipment = shipmentService.getEntityByUuid(shipmentUuid, user);
        ShipmentDto shipmentDto = shipmentMapper.toDto(updatedShipment);
        long timeModified = updatedShipment.getLastModified().getTime();

        assertTrue("Shipment has wrong modified time", (timeFinished > timeModified && timeModified > timeStarted));
        assertNotNull("Shipment doesn't have a modifier", updatedShipment.getCreator());
        assertThat("Shipment was updated with wrong user!",
                updatedShipment.getLastModifier().getToken(), equalTo(user.getToken()));

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentDto);

        assertEquals(expectedJson, actualJson, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void removeShipmentGroup() throws Exception {
        user = testHelper.createUser(testHelper.createCounterparty());
        ShipmentGroup shipmentGroup = testHelper.createShipmentGroupFor(user);
        Shipment shipment = testHelper.createShipmentFor(shipmentGroup, user);

        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
        when().
                delete("/shipments/{uuid}/shipment-group", shipment.getUuid().toString()).
        then().
                body("shipmentGroupUuid", equalTo(null)).
                statusCode(SC_OK);

        testHelper.deleteShipment(shipment);
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
