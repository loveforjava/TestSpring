package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.Address;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.service.AddressService;
import integration.helper.TestHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import static integration.helper.TestHelper.SAME_REGION_COUNTRYSIDE;
import static integration.helper.TestHelper.WRONG_CREATED_MESSAGE;
import static integration.helper.TestHelper.WRONG_LAST_MODIFIED_MESSAGE;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AddressControllerIT extends BaseControllerIT {
    private int addressId = MIN_VALUE;
    @Autowired
    private AddressService addressService;
    @Autowired
    private TestHelper testHelper;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        addressId = (int) testHelper.createAddress().getId();
    }
    
    @After
    public void tearDown() {
        if (addressId != -1) {
            try {
                addressService.delete(addressId);
            } catch (IncorrectInputDataException e) {
                log.debug(e.getMessage());
            }
        }
    }
    
    @Test
    public void getAddresses() throws Exception {
        when().
                get("/addresses").
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getAddress() throws Exception {
        when().
                get("/addresses/{id}", addressId).
        then().
                statusCode(SC_OK).
                body("id", equalTo(addressId));
    }

    @Test
    public void getAddress_notFound() throws Exception {
        when().
                get("/addresses/{id}", addressId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createAddress() throws Exception {
        // create
        JSONObject expectedJson = testHelper.getJsonObjectFromFile("json/address.json");

        long timeStarted = System.currentTimeMillis();
        int newAddressId =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        body(expectedJson.toString()).
                when().
                        post("/addresses").
                then().
                        statusCode(SC_OK).
                        body("countryside", equalTo(false)).
                extract().
                        path("id");
        long timeFinished = System.currentTimeMillis();

        // check created data
        Address createdAddress = addressService.getEntityById(newAddressId);
        long timeCreated = createdAddress.getCreated().getTime();
        long timeModified = createdAddress.getLastModified().getTime();

        assertTrue(WRONG_CREATED_MESSAGE, timeFinished >= timeCreated && timeCreated >= timeStarted);
        assertTrue(WRONG_LAST_MODIFIED_MESSAGE, timeFinished >= timeModified && timeModified >= timeStarted);

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(createdAddress);
        expectedJson.put("countryside", false);

        JSONAssert.assertEquals(expectedJson.toString(), actualJson, false);

        // delete
        addressService.delete(newAddressId);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createAddress_countryside() throws Exception {
        // create
        JSONObject expectedJson = testHelper.getJsonObjectFromFile("json/address.json");
        expectedJson.put("postcode", SAME_REGION_COUNTRYSIDE);

        int newAddressId =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        body(expectedJson.toString()).
                when().
                        post("/addresses").
                then().
                        statusCode(SC_OK).
                        body("countryside", equalTo(true)).
                extract().
                        path("id");

        // check created data
        Address address = addressService.getEntityById(newAddressId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(address);
        expectedJson.put("countryside", true);

        JSONAssert.assertEquals(expectedJson.toString(), actualJson, false);

        // delete
        addressService.delete(newAddressId);
    }

    @Test
    public void updateAddress() throws Exception {
        // update data
        JSONObject expectedJson = testHelper.getJsonObjectFromFile("json/address.json");

        long timeStarted = System.currentTimeMillis();
        given().
                contentType(APPLICATION_JSON_VALUE).
                body(expectedJson.toString()).
        when().
                put("/addresses/{id}", addressId).
        then().
                statusCode(SC_OK);
        long timeFinished = System.currentTimeMillis();

        // check if updated
        Address updatedAddress = addressService.getEntityById(addressId);
        long timeModified = updatedAddress.getLastModified().getTime();

        assertTrue(WRONG_LAST_MODIFIED_MESSAGE, timeFinished >= timeModified && timeModified >= timeStarted);

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(updatedAddress);

        JSONAssert.assertEquals(expectedJson.toString(), actualJson, false);
    }

    @Test
    public void deleteAddress() throws Exception {
        when().
                delete("/addresses/{id}", addressId).
        then().
                statusCode(SC_OK);
        addressId = -1;
    }

    @Test
    public void deleteAddress_notFound() throws Exception {
        when().
                delete("/addresses/{id}", addressId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
