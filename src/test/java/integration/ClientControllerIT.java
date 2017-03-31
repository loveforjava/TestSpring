package integration;

import java.util.UUID;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.service.ClientService;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static java.lang.String.join;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class ClientControllerIT extends BaseControllerIT {
    private Client client;
    private UUID clientUuid;
    private User user;
    @Autowired
    private ClientService clientService;
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
    public void createIndividualClient() throws Exception {
        // create
        Counterparty newCounterparty = testHelper.createCounterparty();

        JSONObject newClientJsonObject = testHelper.getJsonObjectFromFile("json/client-individual.json");
        newClientJsonObject.put("counterpartyUuid", newCounterparty.getUuid().toString());
        newClientJsonObject.put("addressId", (int) testHelper.createAddress().getId());
    
        MockMvcResponse response =
                given().
                        contentType("application/json;charset=UTF-8").
                        queryParam("token", newCounterparty.getUser().getToken()).
                        body(newClientJsonObject.toString()).
                when().
                        post("/clients").
                then().
                        statusCode(SC_OK).
                        extract().
                        response();
        
        UUID newClientUuid = UUID.fromString(response.path("uuid"));

        // check created data
        Client createdClient = clientService.getEntityByUuid(newClientUuid, newCounterparty.getUser());
        
        // manually formulate expected full name data from raw input json
        String expectedFullName = join(" ",
                (String) newClientJsonObject.get("firstName"),
                (String) newClientJsonObject.get("middleName"),
                (String) newClientJsonObject.get("lastName"));
        assertEquals(expectedFullName, createdClient.getName());
        assertEquals(expectedFullName, response.path("name"));

        // delete
        testHelper.deleteClient(createdClient);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void updateIndividualClient() throws Exception {
        // update
        JSONObject updatedClientJsonObject = testHelper.getJsonObjectFromFile("json/client-individual.json");
        updatedClientJsonObject.put("counterpartyUuid", client.getCounterparty().getUuid().toString());
        updatedClientJsonObject.put("addressId", (int) client.getAddress().getId());
        updatedClientJsonObject.put("middleName", "Jakson [edited]");

        given().
                contentType("application/json;charset=UTF-8").
                queryParam("token", user.getToken()).
                body(updatedClientJsonObject.toString()).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                statusCode(SC_OK);

        // check updated data
        Client client = clientService.getEntityByUuid(clientUuid, user);
    
        // manually formulate expected full name data from raw input json
        String expectedFullName = join(" ",
                (String) updatedClientJsonObject.get("firstName"),
                (String) updatedClientJsonObject.get("middleName"),
                (String) updatedClientJsonObject.get("lastName"));
        assertEquals(expectedFullName, client.getName());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void createCompanyClient() throws Exception {
        // create
        Counterparty newCounterparty = testHelper.createCounterparty();
        
        JSONObject newClientJsonObject = testHelper.getJsonObjectFromFile("json/client-company.json");
        newClientJsonObject.put("counterpartyUuid", newCounterparty.getUuid().toString());
        newClientJsonObject.put("addressId", (int) testHelper.createAddress().getId());
        
        MockMvcResponse response =
                given().
                        contentType("application/json;charset=UTF-8").
                        queryParam("token", newCounterparty.getUser().getToken()).
                        body(newClientJsonObject.toString()).
                when().
                        post("/clients").
                then().
                        statusCode(SC_OK).
                        extract().
                        response();
        
        UUID newClientUuid = UUID.fromString(response.path("uuid"));
        
        // check created data
        Client createdClient = clientService.getEntityByUuid(newClientUuid, newCounterparty.getUser());
        
        String expectedFullName = (String) newClientJsonObject.get("name");
        assertEquals(expectedFullName, createdClient.getName());
        assertEquals(expectedFullName, response.path("name"));
        
        // delete
        testHelper.deleteClient(createdClient);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void updateCompanyClient() throws Exception {
        // update
        JSONObject updatedClientJsonObject = testHelper.getJsonObjectFromFile("json/client-company.json");
        updatedClientJsonObject.put("counterpartyUuid", client.getCounterparty().getUuid().toString());
        updatedClientJsonObject.put("addressId", (int) client.getAddress().getId());
        updatedClientJsonObject.put("name", "Rozetka & Roga & Kopyta [edited]");
    
        given().
                contentType("application/json;charset=UTF-8").
                queryParam("token", user.getToken()).
                body(updatedClientJsonObject.toString()).
        when().
                put("/clients/{uuid}", clientUuid.toString()).
        then().
                statusCode(SC_OK);
        
        // check updated data
        Client client = clientService.getEntityByUuid(clientUuid, user);
        
        // manually formulate expected full name data from raw input json
        String expectedFullName = (String) updatedClientJsonObject.get("name");
        assertEquals(expectedFullName, client.getName());
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
