package integration;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.mapper.ClientMapper;
import com.opinta.service.ClientService;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

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
                statusCode(SC_UNAUTHORIZED);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void createClient() throws Exception {
        // create
        Counterparty newCounterparty = testHelper.createCounterparty();

        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/client.json");
        jsonObject.put("counterpartyUuid", newCounterparty.getUuid().toString());
        jsonObject.put("addressId", (int) testHelper.createAddress().getId());
        String expectedJson = jsonObject.toString();

        String newClientIdString =
                given().
                        contentType("application/json;charset=UTF-8").
                        queryParam("token", newCounterparty.getUser().getToken()).
                        body(expectedJson).
                when().
                        post("/clients").
                then().
                        statusCode(SC_OK).
                        extract().
                        path("uuid");
        
        UUID newClientId = UUID.fromString(newClientIdString);

        // check created data
        Client createdClient = clientService.getEntityByUuid(newClientId, newCounterparty.getUser());
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientMapper.toDto(createdClient));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteClient(createdClient);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void updateClient() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/client.json");
        jsonObject.put("counterpartyUuid", client.getCounterparty().getUuid().toString());
        jsonObject.put("addressId", (int) client.getAddress().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                queryParam("token", user.getToken()).
                body(expectedJson).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                statusCode(SC_OK);

        // check updated data
        ClientDto clientDto = clientMapper.toDto(clientService.getEntityByUuid(clientUuid, user));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(clientDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
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
                statusCode(SC_UNAUTHORIZED);
    }
}
