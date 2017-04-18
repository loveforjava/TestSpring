package integration;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.DiscountPerCounterpartyDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Discount;
import com.opinta.entity.DiscountPerCounterparty;
import com.opinta.mapper.DiscountPerCounterpartyMapper;
import com.opinta.service.DiscountPerCounterpartyService;
import com.opinta.service.UserService;
import integration.helper.TestHelper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static com.opinta.util.FormatterUtil.DATE_FORMAT_ISO_8601_24H;
import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.TimeZone.getTimeZone;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class DiscountPerCounterpartyControllerIT extends BaseControllerIT {
    private static final TimeZone UTC = getTimeZone("UTC");

    @Autowired
    private DiscountPerCounterpartyService discountPerCounterpartyService;
    @Autowired
    private DiscountPerCounterpartyMapper discountPerCounterpartyMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private TestHelper testHelper;
    private DiscountPerCounterparty discountPerCounterparty;
    private Counterparty counterparty;
    private JSONParser jsonParser = new JSONParser();
    private SimpleDateFormat simpleDateFormat;
    private ObjectMapper jsonMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        counterparty = testHelper.createCounterparty();
        discountPerCounterparty = testHelper.createDiscountPerCounterparty(testHelper.createDiscount(), counterparty);
        TimeZone.setDefault(UTC);
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_ISO_8601_24H);
        simpleDateFormat.setTimeZone(UTC);
    }
    
    @After
    public void tearDown() {
        testHelper.deleteDiscountPerCounterparty(discountPerCounterparty);
    }
    
    @Test
    public void getDiscountsPerCounterparty() {
        given().
                queryParam("token", userService.getUsersByCounterparty(counterparty).get(0).getToken()).
        when().
                get("/counterparty-discounts").
        then().
                statusCode(SC_OK).
                body("results", hasSize(greaterThan(0)));
    }

    @Test
    public void getDiscountPerCounterparty() {
        given().
                queryParam("token", userService.getUsersByCounterparty(counterparty).get(0).getToken()).
        when().
                get("/counterparty-discounts/{uuid}", discountPerCounterparty.getUuid()).
        then().
                statusCode(SC_OK).
                body("uuid", equalTo(discountPerCounterparty.getUuid().toString()));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void createDiscountPerCounterparty() throws Exception {
        Discount newDiscount = testHelper.createDiscount();
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/discount-per-counterparty.json");
        inputJson.put("discountUuid", newDiscount.getUuid().toString());
        inputJson.put("counterpartyUuid", counterparty.getUuid().toString());

        String newUuid =
                given().
                        queryParam("token", userService.getUsersByCounterparty(counterparty).get(0).getToken()).
                        contentType(APPLICATION_JSON_VALUE).
                        body(inputJson.toString()).
                when().
                        post("/counterparty-discounts").
                then().
                        statusCode(SC_OK).
                extract().
                        path("uuid");
    
        UUID newDiscountUuid = UUID.fromString(newUuid);

        DiscountPerCounterparty discountPerCounterparty = discountPerCounterpartyService.
                getEntityByUuid(newDiscountUuid, userService.getUsersByCounterparty(counterparty).get(0));
        DiscountPerCounterpartyDto discountPerCounterpartyDto = discountPerCounterpartyMapper.
                toDto(discountPerCounterparty);

        JSONObject expectedJson = (JSONObject) jsonParser.parse(inputJson.toJSONString());
        expectedJson.put("uuid", discountPerCounterpartyDto.getUuid());
        
        JSONObject actualJson = (JSONObject) jsonParser.parse(jsonMapper.writeValueAsString(discountPerCounterpartyDto));
        actualJson.put("fromDate", simpleDateFormat.format(discountPerCounterpartyDto.getFromDate()));
        actualJson.put("toDate", simpleDateFormat.format(discountPerCounterpartyDto.getToDate()));
        
        assertEquals(expectedJson.toString(), actualJson.toString(), false);
        
        testHelper.deleteDiscountPerCounterparty(discountPerCounterparty);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void updateDiscountPerCounterparty() throws Exception {
        Discount newDiscount = testHelper.createDiscount();

        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/discount-per-counterparty.json");
        inputJson.put("discountUuid", newDiscount.getUuid().toString());
        inputJson.put("counterpartyUuid", counterparty.getUuid().toString());
        
        given().
                queryParam("token", userService.getUsersByCounterparty(counterparty).get(0).getToken()).
                contentType(APPLICATION_JSON_VALUE).
                body(inputJson.toString()).
        when().
                put("/counterparty-discounts/{uuid}", discountPerCounterparty.getUuid()).
        then().
                body("discountUuid", equalTo(newDiscount.getUuid().toString())).
                statusCode(SC_OK);
    
        DiscountPerCounterparty updatedDiscountPerCounterparty = discountPerCounterpartyService
                .getEntityByUuid(discountPerCounterparty.getUuid(), userService.getUsersByCounterparty(counterparty).get(0));

        assertEquals(updatedDiscountPerCounterparty.getDiscount().getUuid(), newDiscount.getUuid());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void updateDiscountPerCounterparty_discountNotInRange() throws Exception {
        Discount newDiscount = testHelper.createDiscount();

        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/discount-per-counterparty.json");
        inputJson.put("discountUuid", newDiscount.getUuid().toString());
        inputJson.put("counterpartyUuid", counterparty.getUuid().toString());
        inputJson.put("fromDate", "2016-06-01T18:25:43.511Z");
        inputJson.put("toDate", "2016-09-01T18:25:43.511Z");
        
        given().
                queryParam("token", userService.getUsersByCounterparty(counterparty).get(0).getToken()).
                contentType(APPLICATION_JSON_VALUE).
                body(inputJson.toString()).
        when().
                put("/counterparty-discounts/{uuid}", discountPerCounterparty.getUuid()).
        then().
                statusCode(SC_BAD_REQUEST);
        
        DiscountPerCounterparty existedDiscountPerCounterparty = discountPerCounterpartyService
                .getEntityByUuid(discountPerCounterparty.getUuid(),
                        userService.getUsersByCounterparty(discountPerCounterparty.getCounterparty()).get(0));
        
        // make sure entity has not been updated.
        assertEquals(
                discountPerCounterparty.getDiscount().getUuid().toString(),
                existedDiscountPerCounterparty.getDiscount().getUuid().toString());
    }

    @Test
    public void deleteDiscountPerCounterparty() throws Exception {
        given().
                queryParam("token", userService.getUsersByCounterparty(counterparty).get(0).getToken()).
                contentType(APPLICATION_JSON_VALUE).
        when().
                delete("/counterparty-discounts/{uuid}", discountPerCounterparty.getUuid()).
        then().
                statusCode(SC_OK);
    }
}
