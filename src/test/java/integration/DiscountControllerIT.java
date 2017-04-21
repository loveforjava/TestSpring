package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.Discount;
import com.opinta.service.DiscountService;
import integration.helper.TestHelper;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import static com.opinta.util.FormatterUtil.DATE_FORMAT_ISO_8601_24H;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.util.TimeZone.getTimeZone;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class DiscountControllerIT extends BaseControllerIT {
    private static final TimeZone UTC = getTimeZone("UTC");

    @Autowired
    private DiscountService discountService;
    @Autowired
    private TestHelper testHelper;
    private List<Discount> discounts;
    private SimpleDateFormat simpleDateFormat;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        discounts = testHelper.createDiscounts();
        TimeZone.setDefault(UTC);
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_ISO_8601_24H);
        simpleDateFormat.setTimeZone(UTC);
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
        when().
                get("/discounts/{uuid}", discountUuid.toString()).
        then().
                statusCode(SC_OK).
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

        UUID newDiscountUuid = UUID.fromString(newUuid);

        // check created data
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());

        Discount discount = discountService.getEntityByUuid(newDiscountUuid);
        ObjectMapper mapper = new ObjectMapper();
        JSONObject actualJson = (JSONObject) parser.parse(mapper.writeValueAsString(discount));
        actualJson.put("fromDate", simpleDateFormat.format(discount.getFromDate()));
        actualJson.put("toDate", simpleDateFormat.format(discount.getToDate()));

        JSONAssert.assertEquals(expectedJson.toString(), actualJson.toString(), false);

        // delete
        discountService.delete(newDiscountUuid);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateDiscount() throws Exception {
        UUID discountUuid = discounts.get(0).getUuid();

        // update data
        JSONObject inputJson = testHelper.getJsonObjectFromFile("json/discount.json");

        given().
                contentType(APPLICATION_JSON_VALUE).
                body(inputJson.toString()).
        when().
                put("/discounts/{uuid}", discountUuid).
        then().
                statusCode(SC_OK);

        // check updated data
        JSONParser parser = new JSONParser();
        JSONObject expectedJson = (JSONObject) parser.parse(inputJson.toJSONString());

        Discount discount = discountService.getEntityByUuid(discountUuid);
        ObjectMapper mapper = new ObjectMapper();
        JSONObject actualJson = (JSONObject) parser.parse(mapper.writeValueAsString(discount));
        actualJson.put("fromDate", simpleDateFormat.format(discount.getFromDate()));
        actualJson.put("toDate", simpleDateFormat.format(discount.getToDate()));

        JSONAssert.assertEquals(expectedJson.toString(), actualJson.toString(), false);
    }
}
