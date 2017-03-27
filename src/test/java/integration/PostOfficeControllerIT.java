package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.PostOfficeDto;
import com.opinta.mapper.PostOfficeMapper;
import com.opinta.service.PostOfficeService;
import io.restassured.path.json.JsonPath;
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

public class PostOfficeControllerIT extends BaseControllerIT {
    private int postOfficeId = MIN_VALUE;
    @Autowired
    private PostOfficeMapper postOfficeMapper;
    @Autowired
    private PostOfficeService postOfficeService;
    @Autowired
    private DBHelper dbHelper;

    @Before
    public void setUp() throws Exception {
        postOfficeId = (int) dbHelper.createPostOffice().getId();
    }

    @After
    public void tearDown() throws Exception {
        postOfficeService.delete(postOfficeId);
    }

    @Test
    public void getPostOffices() throws Exception {
        when().
                get("/post-offices").
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getPostOffice() throws Exception {
        when().
                get("/post-offices/{id}", postOfficeId).
        then().
                statusCode(SC_OK).
                body("id", equalTo(postOfficeId));
    }

    @Test
    public void getPostOffice_notFound() throws Exception {
        when().
                get("/post-offices/{id}", postOfficeId + 1).
        then()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    public void createPostOffice() throws Exception {
        File file = getFileFromResources("json/post-office.json");
        JsonPath jsonPath = new JsonPath(file);
        int newPostOfficeId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(jsonPath.prettify()).
                when().
                        post("/post-offices").
                then().
                        extract().
                        path("id");
        PostOfficeDto postOfficeDto = postOfficeMapper.toDto(postOfficeService.getEntityById(newPostOfficeId));
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(postOfficeDto);
        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);
        postOfficeService.delete(newPostOfficeId);
    }

    public void updatePostOffice() throws Exception {
        File file = getFileFromResources("json/post-office.json");
        JsonPath jsonPath = new JsonPath(file);

        given().
                contentType("application/json;charset=UTF-8").
                body(jsonPath.prettify()).
        when().
                put("/post-offices/{id}", postOfficeId).
        then().
                statusCode(SC_OK);

        PostOfficeDto postOfficeDto = postOfficeMapper.toDto(postOfficeService.getEntityById(postOfficeId));
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(postOfficeDto);
        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);
    }

    @Test
    public void deletePostOffice() throws Exception {
        when().
                delete("/post-offices/{id}", postOfficeId).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deletePostOffices_notFound() throws Exception {
        when().
                delete("/post-offices/{id}", postOfficeId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
