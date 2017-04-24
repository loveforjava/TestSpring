package integration;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.User;
import com.opinta.mapper.ClientMapper;
import com.opinta.service.ClientService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static integration.helper.TestHelper.NO_CREATOR_MESSAGE;
import static integration.helper.TestHelper.NO_LAST_MODIFIER_MESSAGE;
import static integration.helper.TestHelper.WRONG_CREATED_MESSAGE;
import static integration.helper.TestHelper.WRONG_CREATOR_MESSAGE;
import static integration.helper.TestHelper.WRONG_LAST_MODIFIED_MESSAGE;
import static integration.helper.TestHelper.WRONG_LAST_MODIFIER_MESSAGE;
import static java.lang.String.join;
import static java.lang.String.valueOf;

import static com.opinta.constraint.RegexPattern.POST_ID_LENGTH;
import static com.opinta.entity.ClientType.INDIVIDUAL;
import static com.opinta.util.AlphabetCharactersGenerationUtil.characterOf;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.time.LocalDateTime.now;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
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
        user = testHelper.createUser(testHelper.createCounterparty());
        client = testHelper.createClientFor(user);
        clientUuid = client.getUuid();
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
        user = testHelper.createUser(testHelper.createCounterparty());
        Address newAddress = testHelper.createAddress();

        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("addressId", (int) newAddress.getId());
        inputJson.put("individual", true);

        String firstName = (String) inputJson.get("firstName");
        String middleName = (String) inputJson.get("middleName");
        String lastName = (String) inputJson.get("lastName");
        String expectedFullName = join(" ", lastName, firstName, middleName);

        LocalDateTime timeStarted = now();
        String newUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(inputJson.toString()).
                when().
                        post("/clients").
                then().
                        statusCode(SC_OK).
                        body("name", equalTo(expectedFullName)).
                        body("firstName", equalTo(firstName)).
                        body("middleName", equalTo(middleName)).
                        body("lastName", equalTo(lastName)).
                extract().
                        path("uuid");
        LocalDateTime timeFinished = now();

        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        expectedJson.put("name", expectedFullName);
        expectedJson.put("postId", null);
        expectedJson.remove("externalId");

        UUID newClientUuid = UUID.fromString(newUuid);

        // check created data
        Client createdClient = clientService.getEntityByUuid(newClientUuid, user);
        LocalDateTime timeCreated = createdClient.getCreated();
        LocalDateTime timeModified = createdClient.getLastModified();

        assertTrue(WRONG_CREATED_MESSAGE, timeFinished.isAfter(timeCreated) && timeCreated.isAfter(timeStarted));
        assertTrue(WRONG_LAST_MODIFIED_MESSAGE, timeFinished.isAfter(timeModified) && timeModified.isAfter(timeStarted));
        assertNotNull(NO_CREATOR_MESSAGE, createdClient.getCreator());
        assertNotNull(NO_LAST_MODIFIER_MESSAGE, createdClient.getLastModifier());
        assertThat(WRONG_CREATOR_MESSAGE, createdClient.getCreator().getToken(), equalTo(user.getToken()));
        assertThat(WRONG_LAST_MODIFIER_MESSAGE, createdClient.getLastModifier().getToken(), equalTo(user.getToken()));

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
        user = testHelper.createUser(testHelper.createCounterparty());
        Address newAddress = testHelper.createAddress();

        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("addressId", (int) newAddress.getId());
        inputJson.put("individual", false);

        String expectedFullName = (String) inputJson.get("name");

        LocalDateTime timeStarted = now();
        String newUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(inputJson.toString()).
                when().
                        post("/clients").
                then().
                        statusCode(SC_OK).
                        body("name", equalTo(expectedFullName)).
                        body("firstName", equalTo("")).
                        body("middleName", equalTo("")).
                        body("lastName", equalTo("")).
                extract().
                        path("uuid");
        LocalDateTime timeFinished = now();


        inputJson.remove("postId");
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        // should use hamcrest to check, cuz oracle returns empty string as null, and other db as ""
        expectedJson.remove("firstName");
        expectedJson.remove("middleName");
        expectedJson.remove("lastName");
        expectedJson.put("postId", null);
        expectedJson.remove("externalId");

        UUID newClientUuid = UUID.fromString(newUuid);

        // check created data
        Client createdClient = clientService.getEntityByUuid(newClientUuid, user);
        LocalDateTime timeCreated = createdClient.getCreated();
        LocalDateTime timeModified = createdClient.getLastModified();

        assertTrue(WRONG_CREATED_MESSAGE, timeFinished.isAfter(timeCreated) && timeCreated.isAfter(timeStarted));
        assertTrue(WRONG_LAST_MODIFIED_MESSAGE, timeFinished.isAfter(timeModified) && timeModified.isAfter(timeStarted));
        assertNotNull(NO_CREATOR_MESSAGE, createdClient.getCreator());
        assertNotNull(NO_LAST_MODIFIER_MESSAGE, createdClient.getLastModifier());
        assertThat(WRONG_CREATOR_MESSAGE, createdClient.getCreator().getToken(), equalTo(user.getToken()));
        assertThat(WRONG_LAST_MODIFIER_MESSAGE, createdClient.getLastModifier().getToken(), equalTo(user.getToken()));

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
    public void updateClientAsIndividual() throws Exception {
        // update
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("addressId", (int) client.getAddress().getId());
        inputJson.put("middleName", "Jakson [edited]");
        inputJson.put("phoneNumber", "0934314522");
        inputJson.put("individual", true);

        String firstName = (String) inputJson.get("firstName");
        String middleName = (String) inputJson.get("middleName");
        String lastName = (String) inputJson.get("lastName");

        LocalDateTime timeStarted = now();
        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(inputJson.toString()).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                body("counterpartyUuid", equalTo(client.getCounterparty().getUuid().toString())).
                statusCode(SC_OK);
        LocalDateTime timeFinished = now();

        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        String expectedFullName = join(" ", lastName, firstName, middleName);
        expectedJson.put("name", expectedFullName);
        expectedJson.put("middleName", inputJson.get("middleName"));
        expectedJson.put("postId", null);
        expectedJson.remove("externalId");

        // check updated data
        Client updatedClient = clientService.getEntityByUuid(clientUuid, user);
        LocalDateTime timeModified = updatedClient.getLastModified();

        assertTrue(WRONG_LAST_MODIFIED_MESSAGE, timeFinished.isAfter(timeModified) && timeModified.isAfter(timeStarted));
        assertNotNull(NO_LAST_MODIFIER_MESSAGE, updatedClient.getCreator());
        assertThat(WRONG_LAST_MODIFIER_MESSAGE,
                updatedClient.getLastModifier().getToken(), equalTo(user.getToken()));

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(updatedClient));
        assertEquals(expectedJson.toJSONString(), actualJson, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateClientAsIndividual_postIdNotSaved() throws Exception {
        // update
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("addressId", (int) client.getAddress().getId());
        inputJson.put("middleName", "Jakson [edited]");
        inputJson.put("phoneNumber", "0934314522");
        inputJson.put("individual", true);
        inputJson.put("postId", "P170000001QWE");

        String firstName = (String) inputJson.get("firstName");
        String middleName = (String) inputJson.get("middleName");
        String lastName = (String) inputJson.get("lastName");

        LocalDateTime timeStarted = now();
        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(inputJson.toString()).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                body("counterpartyUuid", equalTo(client.getCounterparty().getUuid().toString())).
                statusCode(SC_OK);
        LocalDateTime timeFinished = now();

        inputJson.remove("postId");
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        String expectedFullName = join(" ", lastName, firstName, middleName);
        expectedJson.put("name", expectedFullName);
        expectedJson.put("middleName", inputJson.get("middleName"));
        expectedJson.put("postId", null);
        expectedJson.remove("externalId");

        // check updated data
        Client updatedClient = clientService.getEntityByUuid(clientUuid, user);
        LocalDateTime timeModified = updatedClient.getLastModified();

        assertTrue(WRONG_LAST_MODIFIED_MESSAGE, timeFinished.isAfter(timeModified) && timeModified.isAfter(timeStarted));
        assertNotNull(NO_LAST_MODIFIER_MESSAGE, updatedClient.getCreator());
        assertThat(WRONG_LAST_MODIFIER_MESSAGE, updatedClient.getLastModifier().getToken(), equalTo(user.getToken()));

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(updatedClient));
        assertEquals(expectedJson.toJSONString(), actualJson, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateClientAsCompany() throws Exception {
        // update
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("addressId", (int) client.getAddress().getId());
        inputJson.put("name", "Rozetka & Roga & Kopyta [edited]");
        inputJson.put("individual", false);

        LocalDateTime timeStarted = now();
        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(inputJson.toString()).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                body("counterpartyUuid", equalTo(client.getCounterparty().getUuid().toString())).
                statusCode(SC_OK);
        LocalDateTime timeFinished = now();

        inputJson.remove("postId");
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        expectedJson.put("name", inputJson.get("name"));
        // should use hamcrest to check, cuz oracle returns empty string as null, and other dbs as ""
        expectedJson.remove("firstName");
        expectedJson.remove("middleName");
        expectedJson.remove("lastName");
        expectedJson.put("postId", null);
        expectedJson.remove("externalId");

        // check updated data
        Client updatedClient = clientService.getEntityByUuid(clientUuid, user);
        LocalDateTime timeModified = updatedClient.getLastModified();

        assertTrue(WRONG_LAST_MODIFIED_MESSAGE, timeFinished.isAfter(timeModified) && timeModified.isAfter(timeStarted));
        assertNotNull(NO_LAST_MODIFIER_MESSAGE, updatedClient.getCreator());
        assertThat(WRONG_LAST_MODIFIER_MESSAGE, updatedClient.getLastModifier().getToken(), equalTo(user.getToken()));

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(updatedClient));
        assertEquals(expectedJson.toJSONString(), actualJson, false);
        assertThat(updatedClient.getFirstName(), anyOf(equalTo(""), equalTo(null)));
        assertThat(updatedClient.getMiddleName(), anyOf(equalTo(""), equalTo(null)));
        assertThat(updatedClient.getLastName(), anyOf(equalTo(""), equalTo(null)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void savingPhoneRemovesAllNonNumericalDigits() throws Exception {
        Address newAddress = testHelper.createAddress();
        user = testHelper.createUser(testHelper.createCounterparty());

        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("phoneNumber", "+(098)-2004-113");
        inputJson.put("addressId", (int) newAddress.getId());
        inputJson.put("individual", false);

        String expectedPhone = "0982004113";

        String newUuid = given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(inputJson.toString()).
        when().
                post("/clients").
        then().
                statusCode(SC_OK).
                body("phoneNumber", equalTo(expectedPhone)).
        extract().
                path("uuid");
        UUID newClientUuid = UUID.fromString(newUuid);
        Client createdClient = clientService.getEntityByUuid(newClientUuid,
                user);
        testHelper.deleteClient(createdClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updatingPhoneRemovesAllNonNumericalDigits() throws Exception {
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("phoneNumber", "+(098)-2004-113");
        inputJson.put("addressId", (int) client.getAddress().getId());
        inputJson.put("individual", false);

        String expectedPhone = "0982004113";

        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(inputJson.toString()).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                body("phoneNumber", equalTo(expectedPhone)).
                statusCode(SC_OK);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void savingPhoneWithNotValidSymbolsReturnsBadRequest() throws Exception {
        Address newAddress = testHelper.createAddress();

        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client.json");
        inputJson.put("phoneNumber", "+(098)-2004-113");
        inputJson.put("addressId", (int) newAddress.getId());
        inputJson.put("phoneNumber", "09820041s24");
        inputJson.put("individual", false);

        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(inputJson.toString()).
        when().
                post("/clients").
        then().
                statusCode(SC_BAD_REQUEST);
    }

    @Test
    public void updateClientPostId() throws Exception {
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/client-type.json");
        inputJson.put("type", INDIVIDUAL.name());

        String postId =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        queryParam("token", user.getToken()).
                        body(inputJson.toString()).
                when().
                        put("/clients/{uuid}/post-id", client.getUuid().toString()).
                then().
                        statusCode(SC_OK).
                extract().
                        path("postId");

        Assert.assertEquals(POST_ID_LENGTH, postId.length());
        Assert.assertEquals(characterOf(INDIVIDUAL), valueOf(postId.charAt(0)));

        Client saved = clientService.getEntityByUuid(client.getUuid(), user);
        Assert.assertEquals(postId, saved.getPostId());
    }
}
