package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.VirtualPostOfficeDto;
import com.opinta.mapper.VirtualPostOfficeMapper;
import com.opinta.service.PostcodePoolService;
import com.opinta.service.VirtualPostOfficeService;
import io.restassured.path.json.JsonPath;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import util.DBHelper;

import java.io.File;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class VirtualPostOfficeControllerIT extends BaseControllerIT {
    private int virtualPostOfficeId = MIN_VALUE;
    @Autowired
    private VirtualPostOfficeService virtualPostOfficeService;
    @Autowired
    private VirtualPostOfficeMapper virtualPostOfficeMapper;
    @Autowired
    private PostcodePoolService postcodePoolService;
    @Autowired
    private DBHelper dbHelper;

    @Before
    public void setUp() throws Exception {
        virtualPostOfficeId = (int) dbHelper.createVirtualPostOffice().getId();
    }

    @After
    public void tearDown() throws Exception {
        virtualPostOfficeService.delete(virtualPostOfficeId);
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
                then()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    public void createVirtualPostOffice() throws Exception {
        File file = getFileFromResources("json/virtual-post-office.json");
        JsonPath jsonPath = new JsonPath(file);
        int newVirtualPostOfficeId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(jsonPath.prettify()).
                when().
                        post("/virtual-post-offices/").
                then().
                        extract().
                        path("id");
        VirtualPostOfficeDto virtualPostOfficeDto = virtualPostOfficeMapper.toDto(virtualPostOfficeService.getEntityById(newVirtualPostOfficeId));
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(virtualPostOfficeDto);
        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);
        virtualPostOfficeService.delete(newVirtualPostOfficeId);
    }

    public void updateVirtualPostOffice() throws Exception {
        File file = getFileFromResources("json/virtual-post-office.json");
        JsonPath jsonPath = new JsonPath(file);

        given().
                contentType("application/json;charset=UTF-8").
                body(jsonPath.prettify()).
                when().
                put("/virtual-post-offices/{id}", virtualPostOfficeId).
                then().
                statusCode(SC_OK);

        VirtualPostOfficeDto virtualPostOfficeDto = virtualPostOfficeMapper
                .toDto(virtualPostOfficeService.getEntityById(virtualPostOfficeId));
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(virtualPostOfficeDto);
        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);
    }

    @Test
    public void deleteVirtualPostOffice() throws Exception {
        when()
                .delete("/virtual-post-offices/{id}", virtualPostOfficeId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteVirtualPostOffice_notFound() throws Exception {
        when()
                .delete("/virtual-post-offices/{id}", virtualPostOfficeId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

}
