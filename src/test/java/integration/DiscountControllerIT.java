package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.Discount;
import com.opinta.service.DiscountService;
import integration.helper.TestHelper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import static integration.helper.AssertHelper.assertDateTimeBetween;
import static integration.helper.TestHelper.WRONG_CREATED_MESSAGE;
import static integration.helper.TestHelper.WRONG_LAST_MODIFIED_MESSAGE;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class DiscountControllerIT extends BaseControllerIT {

    @Autowired
    private DiscountService discountService;
    @Autowired
    private TestHelper testHelper;
    @Autowired
    private ObjectMapper objectMapper;
    private List<Discount> discounts;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        discounts = testHelper.createDiscounts();
    }
    
    @After
    public void tearDown() {
        testHelper.deleteDiscounts(discounts);
    }
    
    @Test
    public void getDiscounts() throws Exception {
        when().
                get("/discounts").
        then().
                statusCode(SC_OK).
                body("results", hasSize(greaterThan(discounts.size()-1)));
    }

    @Test
    public void getDiscount() throws Exception {
        UUID discountUuid = discounts.get(0).getUuid();
        String fromDate = discounts.get(0).getFromDate().format(ISO_LOCAL_DATE);
        String toDate = discounts.get(0).getToDate().format(ISO_LOCAL_DATE);
        when().
                get("/discounts/{uuid}", discountUuid.toString()).
        then().
                statusCode(SC_OK).
                body("fromDate", equalTo(fromDate)).
                body("toDate", equalTo(toDate)).
                body("uuid", equalTo(discountUuid.toString()));
    }

    @Test
    public void getAddress_notFound() throws Exception {
        when().
                get("/discounts/{uuid}", UUID.randomUUID()).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createDiscount() throws Exception {
        // create
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/discount.json");

        LocalDateTime timeStarted = now();
        String newUuid =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        body(inputJson.toString()).
                when().
                        post("/discounts").
                then().
                        statusCode(SC_OK).
                extract().
                        path("uuid");
        LocalDateTime timeFinished = now();
        UUID newDiscountUuid = UUID.fromString(newUuid);

        // check created data
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());

        Discount createdDiscount = discountService.getEntityByUuid(newDiscountUuid);
        LocalDateTime timeCreated = createdDiscount.getCreated();
        LocalDateTime timeModified = createdDiscount.getLastModified();
    
        assertDateTimeBetween(WRONG_CREATED_MESSAGE, timeCreated, timeStarted, timeFinished);
        assertDateTimeBetween(WRONG_LAST_MODIFIED_MESSAGE, timeModified, timeStarted, timeFinished);
    
    
        JSONObject actualJson = (JSONObject) parser.parse(objectMapper.writeValueAsString(createdDiscount));

        JSONAssert.assertEquals(expectedJson.toString(), actualJson.toString(), false);

        // delete
        discountService.delete(newDiscountUuid);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateDiscount() throws Exception {
        UUID discountUuid = discounts.get(0).getUuid();

        // update data
        LocalDateTime timeStarted = now();
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/discount.json");

        given().
                contentType(APPLICATION_JSON_VALUE).
                body(inputJson.toString()).
        when().
                put("/discounts/{uuid}", discountUuid).
        then().
                statusCode(SC_OK);
        LocalDateTime timeFinished = now();

        // check updated data
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());

        Discount updatedDiscount = discountService.getEntityByUuid(discountUuid);
        LocalDateTime timeModified = updatedDiscount.getLastModified();

        assertDateTimeBetween(WRONG_LAST_MODIFIED_MESSAGE, timeModified, timeStarted, timeFinished);

        JSONObject actualJson = (JSONObject) parser.parse(objectMapper.writeValueAsString(updatedDiscount));

        JSONAssert.assertEquals(expectedJson.toString(), actualJson.toString(), false);
    }
}
