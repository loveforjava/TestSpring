package integration;

import com.opinta.entity.Client;
import com.opinta.entity.PostcodePool;
import com.opinta.service.ClientService;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.UserService;
import io.restassured.module.mockmvc.response.MockMvcResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.CounterpartyDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.mapper.CounterpartyMapper;
import com.opinta.service.CounterpartyService;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class CounterpartyControllerIT extends BaseControllerIT {
    private Counterparty counterparty;
    private UUID counterpartyUuid;
    private Client sender;
    private Client recipient;
    private User user;

    @Autowired
    private CounterpartyService counterpartyService;
    @Autowired
    private CounterpartyMapper counterpartyMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private TestHelper testHelper;
    @Autowired
    private ClientService clientService;
    @Autowired
    private PostcodePoolService postcodePoolService;

    @Before
    public void setUp() throws Exception {
        counterparty = testHelper.createCounterparty();
        user = testHelper.createUser(counterparty);
        sender = testHelper.createSenderFor(counterparty);
        recipient = testHelper.createRecipientFor(counterparty);
        counterpartyUuid = counterparty.getUuid();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteClientWithoutDeletingCounterparty(sender);
        testHelper.deleteClientWithoutDeletingCounterparty(recipient);
        testHelper.deleteCounterparty(counterparty);
    }

    @Test
    public void getCounterparties() throws Exception {
        when().
                get("/counterparties").
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK);
    }

    @Test
    public void getCounterparty() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("counterparties/{uuid}", counterpartyUuid.toString()).
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK).
                body("uuid", equalTo(counterpartyUuid.toString()));
    }

    @Test
    public void getCounterparty_notFound() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("/counterparties/{uuid}", UUID.randomUUID().toString()).
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createCounterparty() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/counterparty.json");
        jsonObject.put("postcodePoolUuid", testHelper.createPostcodePool().getUuid().toString());
        String expectedJson = jsonObject.toString();

        MockMvcResponse response =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        body(expectedJson).
                when().
                        post("/counterparties").
                then().
                        contentType(APPLICATION_JSON_VALUE).
                        statusCode(SC_OK).
                extract().response();

        Counterparty createdCounterparty = counterpartyService.getEntityByUuidAnonymous(UUID.fromString(response.path("uuid")));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(counterpartyMapper.toDto(createdCounterparty));
        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteCounterparty(createdCounterparty);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void createSeveralCounterparties_onePostcodePool() throws Exception {
        PostcodePool postcodePool = testHelper.createPostcodePool();
        String fakeName = "Company № ";
        MockMvcResponse response;
        String expectedJson;
        JSONObject inputJson;
        User user;
        Counterparty createdCounterparty;
        List<Counterparty> createdCounterparties = new ArrayList<>();
        String actualJson;
        
        int counterpartiesQty = 3;
        ObjectMapper mapper = new ObjectMapper();
        
        for (int i = 0; i < counterpartiesQty; i++) {
            inputJson = testHelper.getJsonObjectFromFile("json/counterparty.json");
            inputJson.put("postcodePoolUuid", postcodePool.getUuid().toString());
            inputJson.put("name", fakeName + i);
            expectedJson = inputJson.toString();
    
            response =
                    given().
                            contentType(APPLICATION_JSON_VALUE).
                            body(expectedJson).
                    when().
                            post("/counterparties").
                    then().
                            contentType(APPLICATION_JSON_VALUE).
                            statusCode(SC_OK).
                    extract().
                            response();
    
            // check created data
            createdCounterparty = counterpartyService.getEntityByUuidAnonymous(UUID.fromString(response.path("uuid")));
            createdCounterparties.add(createdCounterparty);
            actualJson = mapper.writeValueAsString(counterpartyMapper.toDto(createdCounterparty));
            JSONAssert.assertEquals(expectedJson, actualJson, false);
        }
        
        // delete one by one
        for (Counterparty counterparty : createdCounterparties) {
            testHelper.deleteCounterparty(counterparty);
        }
        
        // make sure that postcode pool remains not affected by associated counterparties removing.
        PostcodePool postcodePoolAfter = postcodePoolService.getEntityByUuid(postcodePool.getUuid());
        assertNotNull(postcodePoolAfter);
        
        // make sure that postcode pool do not hold any counterparties any more.
        List<Counterparty> foundCounterparties = counterpartyService
                .getAllEntitiesByPostcodePoolUuid(postcodePool.getUuid());
        assertEquals(0, foundCounterparties.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateCounterparty() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/counterparty.json");
        jsonObject.put("postcodePoolUuid", counterparty.getPostcodePool().getUuid().toString());
        String expectedJson = jsonObject.toString();

        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(expectedJson).
        when().
                put("/counterparties/{uuid}", counterpartyUuid.toString()).
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK);

        // check updated data
        CounterpartyDto counterpartyDto = counterpartyMapper.toDto(counterpartyService.getEntityByUuid(
                counterpartyUuid, user));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(counterpartyDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteCounterparty() throws Exception {
        testHelper.deleteClientWithoutDeletingCounterparty(sender);
        testHelper.deleteClientWithoutDeletingCounterparty(recipient);
        user.setCounterparty(counterpartyService.getEntityByUuidAnonymous(counterpartyUuid));
        given().
                queryParam("token", user.getToken()).
        when().
                delete("/counterparties/{uuid}", counterpartyUuid.toString()).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteCounterparty_notFound() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                delete("/counterparties/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
