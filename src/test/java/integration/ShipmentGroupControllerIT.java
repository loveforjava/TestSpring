package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.mapper.ShipmentGroupMapper;
import com.opinta.service.ShipmentGroupService;
import com.opinta.service.UserService;
import integration.helper.TestHelper;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
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

        // check created data
        ShipmentGroup createdShipmentGroup = shipmentGroupService.getEntityById(newShipmentGroupId, user);
        long timeCreated = createdShipmentGroup.getCreated().getTime();
        long currentTime = new Date().getTime();
        assertThat("Shipment group was created more than 30 seconds ago!", (currentTime - timeCreated),
                lessThan(30000L));
        assertNotNull("Shipment group doesn't have a creator!", createdShipmentGroup.getCreator());
        assertTrue("Shipment group was created with wrong user!",
                createdShipmentGroup.getCreator().getToken().equals(user.getToken()));

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

        given().
                contentType("application/json;charset=UTF-8").
                queryParam("token", user.getToken()).
                body(expectedJson).
        when().
                put("/shipment-groups/{uuid}", shipmentGroupUuid.toString()).
        then().
                statusCode(SC_OK);

        // check updated data
        ShipmentGroup updatedShipmentGroup = shipmentGroupService.getEntityById(shipmentGroupUuid, user);
        long timeCreated = updatedShipmentGroup.getLastModified().getTime();
        long currentTime = new Date().getTime();
        assertThat("Shipment group was modified more than 30 seconds ago!", (currentTime - timeCreated),
                lessThan(30000L));
        assertNotNull("Shipment group doesn't have a modifier", updatedShipmentGroup.getLastModifier());
        assertTrue("Shipment group was updated with wrong user!",
                updatedShipmentGroup.getLastModifier().getToken().equals(user.getToken()));

        ShipmentGroupDto shipmentGroupDto = shipmentGroupMapper.toDto(updatedShipmentGroup);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentGroupDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }
    
    @Test
    public void deleteShipmentGroup() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                delete("/shipment-groups/{uuid}", shipmentGroupUuid.toString()).
        then().
                statusCode(SC_OK);
    }
    
    @Test
    public void deleteShipmentGroup_notFound() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                delete("/shipment-groups/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
