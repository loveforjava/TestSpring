package integration;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.mapper.ClientMapper;
import com.opinta.service.ClientService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static java.lang.String.join;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class ClientControllerIT extends BaseControllerIT {
    private Client client;
    private UUID clientUuid;
    private User user;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        client = testHelper.createClient();
        clientUuid = client.getUuid();
        user = client.getCounterparty().getUser();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteClient(client);
    }

    @Test
    public void getClients() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("/clients").
        then().
                statusCode(SC_OK);
    }
    
    @Test
    public void getClient() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("clients/{uuid}", clientUuid.toString()).
        then().
                statusCode(SC_OK).
                body("uuid", equalTo(clientUuid.toString()));
    }
    
    @Test
    public void getClient_notFound() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                get("/clients/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void createClientAsIndividual() throws Exception {
        // create
        Counterparty newCounterparty = testHelper.createCounterparty();
        Address newAddress = testHelper.createAddress();

        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("counterpartyUuid", newCounterparty.getUuid().toString());
        inputJson.put("addressId", (int) newAddress.getId());
        inputJson.put("individual", true);
        inputJson.put("sender", true);
    
        String firstName = (String) inputJson.get("firstName");
        String middleName = (String) inputJson.get("middleName");
        String lastName = (String) inputJson.get("lastName");
        String expectedFullName = join(" ", lastName, firstName, middleName);
    
        String newUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", newCounterparty.getUser().getToken()).
                        body(inputJson.toString()).
                when().
                        post("/clients").
                then().
                        statusCode(SC_OK).
                        body("name", equalTo(expectedFullName)).
                        body("firstName", equalTo(firstName)).
                        body("middleName", equalTo(middleName)).
                        body("lastName", equalTo(lastName)).
                        body("sender", equalTo(false)).
                extract().
                        path("uuid");
    
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        expectedJson.put("name", expectedFullName);
        expectedJson.put("sender", false);
        
        UUID newClientUuid = UUID.fromString(newUuid);

        // check created data
        Client createdClient = clientService.getEntityByUuid(newClientUuid, newCounterparty.getUser());
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(createdClient));
        assertEquals(expectedJson.toJSONString(), actualJson, false);

        // delete
        testHelper.deleteClient(createdClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createClientAsCompany() throws Exception {
        // create
        Counterparty newCounterparty = testHelper.createCounterparty();
        Address newAddress = testHelper.createAddress();
        
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("counterpartyUuid", newCounterparty.getUuid().toString());
        inputJson.put("addressId", (int) newAddress.getId());
        inputJson.put("individual", false);
        inputJson.put("sender", true);
        
        String expectedFullName = (String) inputJson.get("name");
        
        String newUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", newCounterparty.getUser().getToken()).
                        body(inputJson.toString()).
                when().
                        post("/clients").
                then().
                        statusCode(SC_OK).
                        body("name", equalTo(expectedFullName)).
                        body("firstName", equalTo("")).
                        body("middleName", equalTo("")).
                        body("lastName", equalTo("")).
                        body("sender", equalTo(false)).
                extract().
                        path("uuid");
    
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        expectedJson.put("sender", false);
        // should use hamcrest to check, cuz oracle returns empty string as null, and other db as ""
        expectedJson.remove("firstName");
        expectedJson.remove("middleName");
        expectedJson.remove("lastName");

        UUID newClientUuid = UUID.fromString(newUuid);
        
        // check created data
        Client createdClient = clientService.getEntityByUuid(newClientUuid, newCounterparty.getUser());
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(createdClient));
        assertEquals(expectedJson.toJSONString(), actualJson, false);
        assertThat(createdClient.getFirstName(), anyOf(equalTo(""), equalTo(null)));
        assertThat(createdClient.getMiddleName(), anyOf(equalTo(""), equalTo(null)));
        assertThat(createdClient.getLastName(), anyOf(equalTo(""), equalTo(null)));
        
        // delete
        testHelper.deleteClient(createdClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createClientAsSender_senderShouldBeCreatedDoesNotMetterWhichSenderMarkPassed() throws Exception {
        float discount = 24.5f;
        // create
        Counterparty newCounterparty = testHelper.createCounterparty();
        Address newAddress = testHelper.createAddress();

        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("counterpartyUuid", newCounterparty.getUuid().toString());
        inputJson.put("addressId", (int) newAddress.getId());
        inputJson.put("individual", false);
        inputJson.put("discount", discount);

        String newUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", newCounterparty.getUser().getToken()).
                        body(inputJson.toString()).
                when().
                        post("/clients/senders").
                then().
                        statusCode(SC_OK).
                        body("sender", equalTo(true)).
                        body("discount", equalTo(discount)).
                extract().
                        path("uuid");

        // check created data
        Client createdClient = clientService.getEntityByUuid(UUID.fromString(newUuid), newCounterparty.getUser());
        assertTrue(createdClient.isSender());
        assertTrue((discount == createdClient.getDiscount()));

        // delete
        testHelper.deleteClient(createdClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateClientAsIndividual() throws Exception {
        // update
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("counterpartyUuid", client.getCounterparty().getUuid().toString());
        inputJson.put("addressId", (int) client.getAddress().getId());
        inputJson.put("middleName", "Jakson [edited]");
        inputJson.put("phoneNumber", "0934314522");
        inputJson.put("individual", true);
        inputJson.put("sender", true);

        String firstName = (String) inputJson.get("firstName");
        String middleName = (String) inputJson.get("middleName");
        String lastName = (String) inputJson.get("lastName");

        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(inputJson.toString()).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                body("sender", equalTo(false)).
                statusCode(SC_OK);

        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        String expectedFullName = join(" ", lastName, firstName, middleName);
        expectedJson.put("name", expectedFullName);
        expectedJson.put("middleName", inputJson.get("middleName"));
        expectedJson.put("sender", false);

        // check updated data
        Client updatedClient = clientService.getEntityByUuid(clientUuid, user);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(updatedClient));
        assertEquals(expectedJson.toJSONString(), actualJson, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateClientAsCompany() throws Exception {
        // update
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("counterpartyUuid", client.getCounterparty().getUuid().toString());
        inputJson.put("addressId", (int) client.getAddress().getId());
        inputJson.put("name", "Rozetka & Roga & Kopyta [edited]");
        inputJson.put("individual", false);

        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(inputJson.toString()).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                statusCode(SC_OK);
    
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        expectedJson.put("name", inputJson.get("name"));
        // should use hamcrest to check, cuz oracle returns empty string as null, and other dbs as ""
        expectedJson.remove("firstName");
        expectedJson.remove("middleName");
        expectedJson.remove("lastName");
        
        // check updated data
        Client updatedClient = clientService.getEntityByUuid(clientUuid, user);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(updatedClient));
        assertEquals(expectedJson.toJSONString(), actualJson, false);
        assertThat(updatedClient.getFirstName(), anyOf(equalTo(""), equalTo(null)));
        assertThat(updatedClient.getMiddleName(), anyOf(equalTo(""), equalTo(null)));
        assertThat(updatedClient.getLastName(), anyOf(equalTo(""), equalTo(null)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateClientAsSender_afterUpdatingSenderMarkShouldRemainTheSame() throws Exception {
        Client clientSender = testHelper.createClientAsSender();
        // update
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("counterpartyUuid", clientSender.getCounterparty().getUuid().toString());
        inputJson.put("addressId", (int) clientSender.getAddress().getId());
        inputJson.put("name", "Rozetka & Roga & Kopyta [edited]");
        inputJson.put("individual", false);
        inputJson.put("sender", false);

        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", clientSender.getCounterparty().getUser().getToken()).
                body(inputJson.toString()).
        when().
                put("/clients/{uuid}", clientSender.getUuid().toString()).
        then().
                body("sender", equalTo(true)).
                statusCode(SC_OK);

        // check updated data
        Client updatedClient = clientService.getEntityByUuid(clientSender.getUuid(),
                clientSender.getCounterparty().getUser());
        assertTrue(updatedClient.isSender());

        // delete
        testHelper.deleteClient(clientSender);
    }
    
    @Test
    public void deleteClient() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                delete("/clients/{uuid}", clientUuid.toString()).
        then().
                statusCode(SC_OK);
    }
    
    @Test
    public void deleteClient_notFound() throws Exception {
        given().
                queryParam("token", user.getToken()).
        when().
                delete("/clients/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
