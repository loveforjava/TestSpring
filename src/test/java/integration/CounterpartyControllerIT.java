package integration;

import com.opinta.entity.Client;
import com.opinta.service.ClientService;
import com.opinta.service.UserService;
import io.restassured.module.mockmvc.response.MockMvcResponse;

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

    @Before
    public void setUp() throws Exception {
        counterparty = testHelper.createCounterparty();
        sender = testHelper.createSenderFor(counterparty);
        recipient = testHelper.createRecipientFor(counterparty);
        counterpartyUuid = counterparty.getUuid();
        user = counterparty.getUser();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteClient(sender);
        testHelper.deleteClient(recipient);
        testHelper.deleteCounterpartyWithPostcodePool(counterparty);
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
    public void getAllClientsOfCounterparty() {
        given().
                queryParam("token", user.getToken()).
        when().
                get("counterparties/{uuid}/clients", counterpartyUuid.toString()).
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK);
    }
    
    @Test
    public void getAllSendersOfCounterparty() {
        given().
                queryParam("token", user.getToken()).
                queryParam("sender", true).
        when().
                get("counterparties/{uuid}/clients", counterpartyUuid.toString()).
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK).
                body("[0].sender", equalTo(true)).
                body("[0].uuid", equalTo(sender.getUuid().toString()));
    }
    
    @Test
    public void getAllRecipientsOfCounterparty() {
        given().
                queryParam("token", user.getToken()).
                queryParam("sender", false).
        when().
                get("counterparties/{uuid}/clients", counterpartyUuid.toString()).
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK).
                body("[0].sender", equalTo(false)).
                body("[0].uuid", equalTo(recipient.getUuid().toString()));
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

        // check created data
        User user = userService.authenticate(UUID.fromString(response.path("token")));
        Counterparty createdCounterparty = counterpartyService.getEntityByUuid(UUID.fromString(response.path("uuid")),
                user);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(counterpartyMapper.toDto(createdCounterparty));
        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteCounterpartyWithPostcodePool(createdCounterparty);
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
    @SuppressWarnings("unchecked")
    public void updateCounterpartyDiscount() throws Exception {
        float newDiscount = 8.0f;

        Client client1 = testHelper.createClient(counterparty);
        Client client2 = testHelper.createClient(counterparty);

        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/counterparty.json");
        jsonObject.put("postcodePoolUuid", counterparty.getPostcodePool().getUuid().toString());
        jsonObject.put("discount", newDiscount);
        String expectedJson = jsonObject.toString();

        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(expectedJson).
        when().
                put("/counterparties/{uuid}/discount", counterpartyUuid.toString()).
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK).
                body("discount", equalTo(newDiscount));

        // check updated data
        CounterpartyDto counterpartyDto = counterpartyMapper.toDto(counterpartyService.getEntityByUuid(
                counterpartyUuid, user));
        assertThat(newDiscount, equalTo(counterpartyDto.getDiscount()));

        List<Client> clients = clientService.getAllEntitiesByCounterpartyUuid(counterpartyUuid, user);
        for (Client client : clients) {
            assertThat(newDiscount, equalTo(client.getDiscount()));
        }

        // delete
        testHelper.deleteClient(client1);
        testHelper.deleteClient(client2);
    }

    @Test
    public void deleteCounterparty() throws Exception {
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
