package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ShipmentDto;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ShipmentService;
import io.restassured.path.json.JsonPath;
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
import static org.hamcrest.Matchers.equalTo;

public class ShipmentControllerIT extends BaseControllerIT {
    private int shipmentId = MIN_VALUE;
    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private DBHelper dbHelper;

    @Before
    public void setUp() throws Exception {
        shipmentId = (int) dbHelper.createShipment().getId();
    }

    @Test
    public void getShipments() throws Exception {
        when().
                get("/shipments").
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getShipment() throws Exception {
        when().
                get("shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK).
                body("id", equalTo(shipmentId));
    }

    @Test
    public void getShipment_notFound() throws Exception {
        when().
                get("/shipments/{id}", shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void createClient() throws Exception {
        File file = getFileFromResources("json/shipment.json");
        JsonPath jsonPath = new JsonPath(file);
        int newShipmentId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(jsonPath.prettify()).
                        when().
                        post("/shipments").
                        then().
                        extract().
                        path("id");
        ShipmentDto shipmentDto = shipmentMapper.toDto(shipmentService.getEntityById(newShipmentId));
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(shipmentDto);
        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);
        shipmentService.delete(newShipmentId);
    }

    public void updateShipment() throws Exception {
        File file = getFileFromResources("json/shipment.json");
        JsonPath jsonPath = new JsonPath(file);

        given().
                contentType("application/json;charset=UTF-8").
                body(jsonPath.prettify()).
                when().
                put("/shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK);

        ShipmentDto shipmentDto = shipmentMapper.toDto(shipmentService.getEntityById(shipmentId));
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(shipmentDto);
        JSONAssert.assertEquals(jsonPath.prettify(), jsonString, false);
    }

    @Test
    public void deleteShipment() throws Exception {
        when()
                .delete("/shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteShipment_notFound() throws Exception {
        when()
                .delete("/shipments/{id}", shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }
}
