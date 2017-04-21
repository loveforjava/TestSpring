package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.entity.User;
import com.opinta.mapper.UserMapper;
import com.opinta.service.UserService;
import integration.helper.TestHelper;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static integration.helper.TestHelper.WRONG_CREATED_MESSAGE;
import static integration.helper.TestHelper.WRONG_LAST_MODIFIED_MESSAGE;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.junit.Assert.assertTrue;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class UserControllerIT extends BaseControllerIT {
    @Autowired
    private TestHelper testHelper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Test
    public void createUser() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/user.json");
        jsonObject.put("counterpartyUuid", testHelper.createCounterparty().getUuid().toString());
        String expectedJson = jsonObject.toString();

        long timeStarted = System.currentTimeMillis();
        MockMvcResponse response =
                given().
                        contentType(APPLICATION_JSON_VALUE).
                        body(expectedJson).
                when().
                        post("/users").
                then().
                        contentType(APPLICATION_JSON_VALUE).
                        statusCode(SC_OK).
                extract()
                        .response();
        long timeFinished = System.currentTimeMillis();

        User createdUser = userService.getEntityByToken(UUID.fromString(response.path("token")));
        long timeCreated = createdUser.getCreated().getTime();
        long timeModified = createdUser.getLastModified().getTime();

        assertTrue(WRONG_CREATED_MESSAGE, (timeFinished > timeCreated && timeCreated > timeStarted));
        assertTrue(WRONG_LAST_MODIFIED_MESSAGE, (timeFinished > timeModified && timeModified > timeStarted));

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(userMapper.toDto(createdUser));
        JSONAssert.assertEquals(expectedJson, actualJson, false);
        testHelper.deleteUser(createdUser);
    }
}
