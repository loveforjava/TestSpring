package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.mapper.ShipmentGroupMapper;
import com.opinta.service.ShipmentGroupService;
import integration.helper.TestHelper;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static integration.helper.AssertHelper.assertTimeBetween;
import static integration.helper.TestHelper.NO_CREATOR_MESSAGE;
import static integration.helper.TestHelper.NO_LAST_MODIFIER_MESSAGE;
import static integration.helper.TestHelper.WRONG_CREATED_MESSAGE;
import static integration.helper.TestHelper.WRONG_CREATOR_MESSAGE;
import static integration.helper.TestHelper.WRONG_LAST_MODIFIED_MESSAGE;
import static integration.helper.TestHelper.WRONG_LAST_MODIFIER_MESSAGE;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.time.LocalDateTime.now;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

public class ShipmentGroupControllerIT extends BaseControllerIT {
    private ShipmentGroup shipmentGroup;
    private UUID shipmentGroupUuid;
    private User user;
    private Counterparty counterparty;
    @Autowired
    private ShipmentGroupService shipmentGroupService;
    @Autowired
    private ShipmentGroupMapper shipmentGroupMapper;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        counterparty = testHelper.createCounterparty();
        user = testHelper.createUser(counterparty);
        shipmentGroup = testHelper.createShipmentGroupFor(counterparty);
        shipmentGroupUuid = shipmentGroup.getUuid();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteShipmentGroup(shipmentGroup);
    }

    @Test
    public void getShipmentGroups() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("/shipment-groups").
        then().
                statusCode(SC_OK);
    }
    
    @Test
    public void getShipmentGroup() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("shipment-groups/{uuid}", shipmentGroupUuid.toString()).
        then().
                statusCode(SC_OK).
                body("uuid", equalTo(shipmentGroupUuid.toString()));
    }

    @Test
    public void getShipmentGroupForm() throws Exception {
        Shipment shipment = testHelper.createShipmentWithSameCounterparty(shipmentGroup,
                shipmentGroup.getCounterparty());

        given().
                queryParam("token", user.getToken()).
        when().
                get("shipment-groups/{uuid}/form", shipmentGroupUuid.toString()).
        then().
                statusCode(SC_OK).
                contentType(APPLICATION_PDF_VALUE);
        // delete shipment
        testHelper.deleteShipment(shipment);
    }

    @Test
    public void getShipmentGroupForm103() throws Exception {
        Shipment shipment = testHelper.createShipmentWithSameCounterparty(shipmentGroup,
                shipmentGroup.getCounterparty());
        given().
                queryParam("token", user.getToken()).
        when().
                get("shipment-groups/{uuid}/form103", shipmentGroupUuid.toString()).
        then().
                statusCode(SC_OK).
                contentType(APPLICATION_PDF_VALUE);
        // delete shipment
        testHelper.deleteShipment(shipment);
    }

    @Test
    public void getShipmentGroup_notFound() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("/shipment-groups/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void createShipmentGroup() throws Exception {
        // create
        Counterparty newCounterparty = testHelper.createCounterparty();
        user = testHelper.createUser(newCounterparty);

        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment-group.json");
        jsonObject.put("counterpartyUuid", newCounterparty.getUuid().toString());
        String expectedJson = jsonObject.toString();

        LocalDateTime timeStarted = now();
        String newShipmentGroupIdString =
                given().
                        contentType("application/json;charset=UTF-8").
                        queryParam("token", user.getToken()).
                        body(expectedJson).
                when().
                        post("/shipment-groups").
                then().
                        statusCode(SC_OK).
                extract().
                        path("uuid");
        
        UUID newShipmentGroupId = UUID.fromString(newShipmentGroupIdString);
        LocalDateTime timeFinished = now();

        // check created data
        ShipmentGroup createdShipmentGroup = shipmentGroupService.getEntityById(newShipmentGroupId, user);
        LocalDateTime timeCreated = createdShipmentGroup.getCreated();
        LocalDateTime timeModified = createdShipmentGroup.getLastModified();

        assertTimeBetween(WRONG_CREATED_MESSAGE, timeStarted, timeCreated, timeFinished);
        assertTimeBetween(WRONG_LAST_MODIFIED_MESSAGE, timeStarted, timeModified, timeFinished);
        assertNotNull(NO_CREATOR_MESSAGE, createdShipmentGroup.getCreator());
        assertNotNull(NO_LAST_MODIFIER_MESSAGE, createdShipmentGroup.getLastModifier());
        assertThat(WRONG_CREATOR_MESSAGE, createdShipmentGroup.getCreator().getToken(), equalTo(user.getToken()));
        assertThat(WRONG_LAST_MODIFIER_MESSAGE,
                createdShipmentGroup.getLastModifier().getToken(), equalTo(user.getToken()));

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentGroupMapper.toDto(createdShipmentGroup));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipmentGroup(createdShipmentGroup);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void updateShipmentGroup() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment-group.json");
        jsonObject.put("counterpartyUuid", shipmentGroup.getCounterparty().getUuid().toString());
        String expectedJson = jsonObject.toString();

        LocalDateTime timeStarted = now();
        given().
                contentType("application/json;charset=UTF-8").
                queryParam("token", user.getToken()).
                body(expectedJson).
        when().
                put("/shipment-groups/{uuid}", shipmentGroupUuid.toString()).
        then().
                statusCode(SC_OK);
        LocalDateTime timeFinished = now();

        // check updated data
        ShipmentGroup updatedShipmentGroup = shipmentGroupService.getEntityById(shipmentGroupUuid, user);
        LocalDateTime timeModified = updatedShipmentGroup.getLastModified();

        assertTimeBetween(WRONG_LAST_MODIFIED_MESSAGE, timeStarted, timeModified, timeFinished);
        assertNotNull(NO_LAST_MODIFIER_MESSAGE, updatedShipmentGroup.getLastModifier());
        assertThat(WRONG_LAST_MODIFIER_MESSAGE,
                updatedShipmentGroup.getLastModifier().getToken(), equalTo(user.getToken()));

        ShipmentGroupDto shipmentGroupDto = shipmentGroupMapper.toDto(updatedShipmentGroup);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentGroupDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }
}
