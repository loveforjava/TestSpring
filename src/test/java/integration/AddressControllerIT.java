package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.Address;
import com.opinta.service.AddressService;
import integration.helper.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static org.hamcrest.CoreMatchers.equalTo;

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
        addressService.delete(addressId);
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
    public void createAddress() throws Exception {
        // create
        String expectedJson = testHelper.getJsonFromFile("json/address.json");

        int newAddressId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                when().
                        post("/addresses").
                then().
                        extract().
                        path("id");

        // check created data
        Address address = addressService.getEntityById(newAddressId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(address);

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        addressService.delete(newAddressId);
    }

    @Test
    public void updateAddress() throws Exception {
        // update data
        String expectedJson = testHelper.getJsonFromFile("json/address.json");

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
        when().
                put("/addresses/{id}", addressId).
        then().
                statusCode(SC_OK);

        // check if updated
        Address address = addressService.getEntityById(addressId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(address);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteAddress() throws Exception {
        when()
                .delete("/addresses/{id}", addressId).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteAddress_notFound() throws Exception {
        when()
                .delete("/addresses/{id}", addressId+1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
