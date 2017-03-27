package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ClientDto;
import com.opinta.entity.Address;
import com.opinta.entity.Client;
import com.opinta.entity.PostcodePool;
import com.opinta.entity.VirtualPostOffice;
import com.opinta.mapper.ClientMapper;
import com.opinta.service.AddressService;
import com.opinta.service.ClientService;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.VirtualPostOfficeService;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import util.DBHelper;

import javax.transaction.Transactional;
import java.io.File;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class ClientControllerIT extends BaseControllerIT {
    private int clientId = MIN_VALUE;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private DBHelper dbHelper;

    @Before
    public void setUp() throws Exception {
        clientId = (int) dbHelper.createClient().getId();
    }

    @After
    public void tearDown() throws Exception {
        clientService.delete(clientId);
    }

    @Test
    public void getClients() throws Exception {
        when().
                get("/clients").
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getClient() throws Exception {
        when().
                get("clients/{id}", clientId).
                then().
                statusCode(SC_OK).
                body("id", equalTo(clientId));
    }

    @Test
    public void getClient_notFound() throws Exception {
        when().
                get("/clients/{id}", clientId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void createClient() throws Exception {
        File file = getFileFromResources("json/client.json");
        JsonPath jsonPath = new JsonPath(file);
        int newClientId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(jsonPath.prettify()).
                when().
                        post("/clients").
                then().
                        extract().
                        path("id");
        ClientDto clientDto = clientMapper.toDto(clientService.getEntityById(newClientId));
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(clientDto);
        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);
        clientService.delete(newClientId);
    }

    public void updateClient() throws Exception {
        File file = getFileFromResources("json/client.json");
        JsonPath jsonPath = new JsonPath(file);

        given().
                contentType("application/json;charset=UTF-8").
                body(jsonPath.prettify()).
        when().
                put("/clients/{id}", clientId).
        then().
                statusCode(SC_OK);

        ClientDto clientDto = clientMapper.toDto(clientService.getEntityById(clientId));
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(clientDto);
        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);
    }

    @Test
    public void deleteClient() throws Exception {
        when()
                .delete("/clients/{id}", clientId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteClient_notFound() throws Exception {
        when()
                .delete("/clients/{id}", clientId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }
}
