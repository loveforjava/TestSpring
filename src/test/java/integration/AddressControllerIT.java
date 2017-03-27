package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.Address;
import com.opinta.service.AddressService;
import io.restassured.path.json.JsonPath;
import java.io.File;
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

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        Address address = new Address("00001", "Ternopil", "Monastiriska",
                "Monastiriska", "Sadova", "51", "");
        addressId = (int) addressService.saveEntity(address).getId();
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
        File file = getFileFromResources("json/address.json");
        JsonPath jsonPath = new JsonPath(file);
        int newAddressId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(jsonPath.prettify()).
                when().
                        post("/addresses").
                then().
                        extract().
                        path("id");

        // check if created
        Address address = addressService.getEntityById(newAddressId);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(address);

        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);

        // delete
        addressService.delete(newAddressId);
    }

    @Test
    public void updateAddress() throws Exception {
        // update data
        File file = getFileFromResources("json/address.json");
        JsonPath jsonPath = new JsonPath(file);

        given().
                contentType("application/json;charset=UTF-8").
                body(jsonPath.prettify()).
        when().
                put("/addresses/{id}", addressId).
        then().
                statusCode(SC_OK);

        // check if updated
        Address address = addressService.getEntityById(addressId);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(address);

        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);
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
