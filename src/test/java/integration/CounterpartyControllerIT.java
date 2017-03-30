package integration;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.CounterpartyDto;
import com.opinta.entity.Counterparty;
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

public class CounterpartyControllerIT extends BaseControllerIT {
    private Counterparty counterparty;
    private UUID counterpartyUuid;

    @Autowired
    private CounterpartyService counterpartyService;
    @Autowired
    private CounterpartyMapper counterpartyMapper;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        counterparty = testHelper.createCounterparty();
        counterpartyUuid = counterparty.getUuid();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteCounterpartyWithPostcodePool(counterparty);
    }

    @Test
    public void getCounterparties() throws Exception {
        when().
                get("/counterparties").
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getCounterparty() throws Exception {
        when().
                get("counterparties/{id}", counterpartyUuid.toString()).
        then().
                statusCode(SC_OK).
                body("uuid", equalTo(counterpartyUuid.toString()));
    }

    @Test
    public void getCounterparty_notFound() throws Exception {
        when().
                get("/counterparties/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createCounterparty() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/counterparty.json");
        jsonObject.put("postcodePoolId", (int) testHelper.createPostcodePool().getId());
        String expectedJson = jsonObject.toString();

        String newCounterpartyIdString =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                when().
                        post("/counterparties/").
                then().
                        extract().
                        path("uuid");
        
        UUID newCounterpartyId = UUID.fromString(newCounterpartyIdString);
        
        // check created data
        Counterparty createdCounterparty = counterpartyService.getEntityByUuid(newCounterpartyId);
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
        jsonObject.put("postcodePoolId", (int) testHelper.createPostcodePool().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
        when().
                put("/counterparties/{uuid}", counterpartyUuid.toString()).
        then().
                statusCode(SC_OK);

        // check updated data
        CounterpartyDto counterpartyDto = counterpartyMapper
                .toDto(counterpartyService.getEntityByUuid(counterpartyUuid));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(counterpartyDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteCounterparty() throws Exception {
        when().
                delete("/counterparties/{uuid}", counterpartyUuid.toString()).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteCounterparty_notFound() throws Exception {
        when().
                delete("/counterparties/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
