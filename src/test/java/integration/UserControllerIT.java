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

import java.time.LocalDateTime;
import java.util.UUID;

import static integration.helper.AssertHelper.assertDateTimeBetween;
import static integration.helper.TestHelper.WRONG_CREATED_MESSAGE;
import static integration.helper.TestHelper.WRONG_LAST_MODIFIED_MESSAGE;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.time.LocalDateTime.now;

import static javax.servlet.http.HttpServletResponse.SC_OK;
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

        LocalDateTime timeStarted = now();
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
        LocalDateTime timeFinished = now();

        User createdUser = userService.getEntityByToken(UUID.fromString(response.path("token")));
        LocalDateTime timeCreated = createdUser.getCreated();
        LocalDateTime timeModified = createdUser.getLastModified();

        assertDateTimeBetween(WRONG_CREATED_MESSAGE, timeCreated, timeStarted, timeFinished);
        assertDateTimeBetween(WRONG_LAST_MODIFIED_MESSAGE, timeModified, timeStarted, timeFinished);

        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(userMapper.toDto(createdUser));
        JSONAssert.assertEquals(expectedJson, actualJson, false);
        testHelper.deleteUser(createdUser);
    }
}
