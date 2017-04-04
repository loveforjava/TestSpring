package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.entity.PostcodePool;
import com.opinta.mapper.PostcodePoolMapper;
import com.opinta.service.PostcodePoolService;
import integration.helper.TestHelper;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
public class PostcodePoolIT extends BaseControllerIT {
    private UUID postcodePoolUuid;
    private PostcodePool postcodePool;
    @Autowired
    private PostcodePoolService postcodePoolService;
    @Autowired
    private PostcodePoolMapper postcodePoolMapper;
    @Autowired
    private TestHelper testHelper;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        postcodePool = testHelper.createPostcodePool();
        postcodePoolUuid = postcodePool.getUuid();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deletePostcodePool(postcodePool);
    }

    @Test
    public void getPostcodePools() throws Exception {
        when().
                get("/postcodes").
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getPostcodePool() throws Exception {
        when().
                get("/postcodes/{uuid}", postcodePoolUuid).
        then().
                statusCode(SC_OK).
                body("uuid", equalTo(postcodePoolUuid.toString()));
    }

    @Test
    public void getClient_notFound() throws Exception {
        when().
                get("/postcodes/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createPostcodePool() throws Exception {
        // create
        JSONObject expectedJson = testHelper.getJsonObjectFromFile("json/postcode-pool.json");

        MockMvcResponse response =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        body(expectedJson.toString()).
                when().
                        post("/postcodes").
                then().
                        statusCode(SC_OK).
                extract().
                        response();

        // check created data
        PostcodePool createdPostcodePool = postcodePoolService.getEntityByUuid(UUID.fromString(response.path("uuid")));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(postcodePoolMapper.toDto(createdPostcodePool));

        JSONAssert.assertEquals(expectedJson.toString(), actualJson, false);

        // delete
        testHelper.deletePostcodePool(createdPostcodePool);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updatePostcodePool() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/postcode-pool.json");
        String expectedJson = jsonObject.toString();

        given().
                contentType(APPLICATION_JSON_VALUE).
                body(expectedJson).
        when().
                put("/postcodes/{uuid}", postcodePoolUuid.toString()).
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK);

        // check updated data
        PostcodePoolDto postcodePoolDto = postcodePoolMapper.toDto(postcodePoolService.getEntityByUuid(
                postcodePoolUuid));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(postcodePoolDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deletePostcodePool() throws Exception {
        given().
                when().
                delete("/postcodes/{uuid}", postcodePoolUuid.toString()).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deletePostcodePool_notFound() throws Exception {
        given().
                when().
                delete("/postcodes/{uuid}", UUID.randomUUID().toString()).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
