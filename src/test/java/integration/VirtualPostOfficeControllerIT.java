package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.VirtualPostOfficeDto;
import com.opinta.entity.VirtualPostOffice;
import com.opinta.mapper.VirtualPostOfficeMapper;
import com.opinta.service.VirtualPostOfficeService;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class VirtualPostOfficeControllerIT extends BaseControllerIT {
    private VirtualPostOffice virtualPostOffice;
    private int virtualPostOfficeId = MIN_VALUE;

    @Autowired
    private VirtualPostOfficeService virtualPostOfficeService;
    @Autowired
    private VirtualPostOfficeMapper virtualPostOfficeMapper;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        virtualPostOffice = testHelper.createVirtualPostOffice();
        virtualPostOfficeId = (int) virtualPostOffice.getId();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteVirtualPostOfficeWithPostcodePool(virtualPostOffice);
    }

    @Test
    public void getVirtualPostOffices() throws Exception {
        when().
                get("/virtual-post-offices").
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getVirtualPostOffice() throws Exception {
        when().
                get("virtual-post-offices/{id}", virtualPostOfficeId).
        then().
                statusCode(SC_OK).
                body("id", equalTo(virtualPostOfficeId));
    }

    @Test
    public void getVirtualPostOffice_notFound() throws Exception {
        when().
                get("/virtual-post-offices/{id}", virtualPostOfficeId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createVirtualPostOffice() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/virtual-post-office.json");
        jsonObject.put("activePostcodePoolId", (int) testHelper.createPostcodePool().getId());
        String expectedJson = jsonObject.toString();

        int newVirtualPostOfficeId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                when().
                        post("/virtual-post-offices/").
                then().
                        extract().
                        path("id");

        // check created data
        VirtualPostOffice createdVirtualPostOffice = virtualPostOfficeService.getEntityById(newVirtualPostOfficeId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(virtualPostOfficeMapper.toDto(createdVirtualPostOffice));
        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteVirtualPostOfficeWithPostcodePool(createdVirtualPostOffice);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateVirtualPostOffice() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/virtual-post-office.json");
        jsonObject.put("activePostcodePoolId", (int) testHelper.createPostcodePool().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
        when().
                put("/virtual-post-offices/{id}", virtualPostOfficeId).
        then().
                statusCode(SC_OK);

        // check updated data
        VirtualPostOfficeDto virtualPostOfficeDto = virtualPostOfficeMapper
                .toDto(virtualPostOfficeService.getEntityById(virtualPostOfficeId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(virtualPostOfficeDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteVirtualPostOffice() throws Exception {
        when().
                delete("/virtual-post-offices/{id}", virtualPostOfficeId).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteVirtualPostOffice_notFound() throws Exception {
        when().
                delete("/virtual-post-offices/{id}", virtualPostOfficeId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
