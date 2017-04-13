package integration;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.DiscountPerCounterpartyDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Discount;
import com.opinta.entity.DiscountPerCounterparty;
import com.opinta.mapper.DiscountPerCounterpartyMapper;
import com.opinta.service.CounterpartyService;
import com.opinta.service.DiscountPerCounterpartyService;
import com.opinta.service.DiscountService;
import integration.helper.TestHelper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.TimeZone.getTimeZone;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class DiscountPerCounterpartyControllerIT extends BaseControllerIT {
    private static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final TimeZone UTC = getTimeZone("UTC");

    @Autowired
    private DiscountPerCounterpartyService discountPerCounterpartyService;
    @Autowired
    private CounterpartyService counterpartyService;
    @Autowired
    private DiscountService discountService;
    @Autowired
    private DiscountPerCounterpartyMapper discountPerCounterpartyMapper;
    @Autowired
    private TestHelper testHelper;
    private List<Discount> discounts;
    private List<DiscountPerCounterparty> discountsPerCounterparty;
    private Counterparty counterparty;
    private JSONParser jsonParser = new JSONParser();
    private SimpleDateFormat simpleDateFormat;
    
    @Before
    public void setUp() throws Exception {
        counterparty = testHelper.createCounterparty();
        discounts = testHelper.createDiscounts();
        discountsPerCounterparty = testHelper.createDiscountsPerCounterparty(discounts, counterparty);
        TimeZone.setDefault(UTC);
        simpleDateFormat = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
        simpleDateFormat.setTimeZone(UTC);
    }
    
    @After
    public void tearDown() throws Exception {
        testHelper.deleteDiscountsPerCounterparty(discountsPerCounterparty);
        testHelper.deleteDiscounts(discounts);
        testHelper.deleteCounterparty(counterparty);
    }
    
    @Test
    public void getDiscountsPerCounterparty() {
        given().
                queryParam("token", counterparty.getUser().getToken()).
        when().
                get("/discounts-per-counterparty").
        then().
                statusCode(SC_OK).
                body("results", hasSize(greaterThan(discountsPerCounterparty.size()-1)));
    }
    
    @Test
    public void getDiscountPerCounterparty() {
        UUID discountPerCounterpartyUuid = discountsPerCounterparty.get(0).getUuid();
        given().
                queryParam("token", counterparty.getUser().getToken()).
        when().
                get("/discounts-per-counterparty/{uuid}", discountPerCounterpartyUuid.toString()).
        then().
                statusCode(SC_OK).
                body("uuid", equalTo(discountPerCounterpartyUuid.toString()));
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
                        queryParam("token", counterparty.getUser().getToken()).
                        contentType(APPLICATION_JSON_VALUE).
                        body(inputJson.toString()).
                when().
                        post("/discounts-per-counterparty").
                then().
                        statusCode(SC_OK).
                extract().
                        path("uuid");
    
        UUID newDiscountUuid = UUID.fromString(newUuid);

        DiscountPerCounterparty discountPerCounterparty = discountPerCounterpartyService.
                getEntityByUuid(newDiscountUuid, counterparty.getUser());
        DiscountPerCounterpartyDto discountPerCounterpartyDto = discountPerCounterpartyMapper.
                toDto(discountPerCounterparty);

        JSONObject expectedJson = (JSONObject) jsonParser.parse(inputJson.toJSONString());
        expectedJson.put("uuid", discountPerCounterpartyDto.getUuid());
    
        ObjectMapper mapper = new ObjectMapper();
        JSONObject actualJson = (JSONObject) jsonParser.parse(mapper.writeValueAsString(discountPerCounterpartyDto));
        actualJson.put("fromDate", simpleDateFormat.format(discountPerCounterpartyDto.getFromDate()));
        actualJson.put("toDate", simpleDateFormat.format(discountPerCounterpartyDto.getToDate()));
        
        assertEquals(expectedJson.toString(), actualJson.toString(), false);
        
        testHelper.deleteDiscountPerCounterparty(discountPerCounterparty);
    }
    
    @Test
    public void updateDiscountPerCounterparty() {
        
    }
    
    @Test
    public void deleteDiscountPerCounterparty() {
        
    }
}
