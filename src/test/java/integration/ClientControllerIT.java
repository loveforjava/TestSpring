package integration;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.mapper.ClientMapper;
import com.opinta.service.ClientService;
import com.opinta.service.UserService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static java.lang.String.join;
import static java.lang.String.valueOf;

import static com.opinta.entity.ClientType.INDIVIDUAL;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
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
    private UserService userService;
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
        // fake postId, cannot be saved
        inputJson.put("postId", "Z170000001QFR");
        
        String firstName = (String) inputJson.get("firstName");
        String middleName = (String) inputJson.get("middleName");
        String lastName = (String) inputJson.get("lastName");
        String expectedFullName = join(" ", lastName, firstName, middleName);

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
    
        inputJson.remove("postId");
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        expectedJson.put("name", expectedFullName);
        expectedJson.remove("customId");
        
        UUID newClientUuid = UUID.fromString(newUuid);

        // check created data
        Client createdClient = clientService.getEntityByUuid(newClientUuid, user);
        Assert.assertEquals(inputJson.get("customId"), createdClient.getCustomId());
        Assert.assertNull(createdClient.getPostId());
        
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
        // fake postId, cannot be saved
        inputJson.put("postId", "Z170000001QFR");
        
        String expectedFullName = (String) inputJson.get("name");

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
        
        inputJson.remove("postId");
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        // should use hamcrest to check, cuz oracle returns empty string as null, and other db as ""
        expectedJson.remove("firstName");
        expectedJson.remove("middleName");
        expectedJson.remove("lastName");
        expectedJson.remove("customId");

        UUID newClientUuid = UUID.fromString(newUuid);

        // check created data
        Client createdClient = clientService.getEntityByUuid(newClientUuid, user);
        Assert.assertEquals(inputJson.get("customId"), createdClient.getCustomId());
        Assert.assertNull(createdClient.getPostId());
        
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
        inputJson.put("customId", "11111-fffff-xxx-9876");
        // fake postId, cannot be saved
        inputJson.put("postId", "Z170000001QFR");
        
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
                body("counterpartyUuid", equalTo(client.getCounterparty().getUuid().toString())).
                statusCode(SC_OK);
    
        inputJson.remove("postId");
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        String expectedFullName = join(" ", lastName, firstName, middleName);
        expectedJson.put("name", expectedFullName);
        expectedJson.put("middleName", inputJson.get("middleName"));
        expectedJson.remove("customId");

        // check updated data
        Client updatedClient = clientService.getEntityByUuid(clientUuid, user);
        Assert.assertEquals(inputJson.get("customId"), updatedClient.getCustomId());
        Assert.assertNull(updatedClient.getPostId());
        
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
        // fake postId, cannot be saved
        inputJson.put("postId", "Z170000001QFR");

        given().
                contentType(APPLICATION_JSON_VALUE).
                queryParam("token", user.getToken()).
                body(inputJson.toString()).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                body("counterpartyUuid", equalTo(client.getCounterparty().getUuid().toString())).
                statusCode(SC_OK);
    
        inputJson.remove("postId");
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());
        expectedJson.put("name", inputJson.get("name"));
        // should use hamcrest to check, cuz oracle returns empty string as null, and other dbs as ""
        expectedJson.remove("firstName");
        expectedJson.remove("middleName");
        expectedJson.remove("lastName");
        expectedJson.remove("customId");
        
        // check updated data
        Client updatedClient = clientService.getEntityByUuid(clientUuid, user);
        Assert.assertEquals(inputJson.get("customId"), updatedClient.getCustomId());
        Assert.assertNull(updatedClient.getPostId());
    
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(updatedClient));
        assertEquals(expectedJson.toJSONString(), actualJson, false);
        assertThat(updatedClient.getFirstName(), anyOf(equalTo(""), equalTo(null)));
        assertThat(updatedClient.getMiddleName(), anyOf(equalTo(""), equalTo(null)));
        assertThat(updatedClient.getLastName(), anyOf(equalTo(""), equalTo(null)));
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
    public void verifyExistingClientAndAssignPostId() throws Exception {
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
        
        Assert.assertEquals(13, postId.length());
        Assert.assertEquals(INDIVIDUAL.postIdLetter(), valueOf(postId.charAt(0)));
        
        Client saved = clientService.getEntityByUuid(client.getUuid(), user);
        Assert.assertEquals(postId, saved.getPostId());
    }
}
