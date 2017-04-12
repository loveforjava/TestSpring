package integration;

import com.opinta.entity.classifier.TariffGrid;
import com.opinta.service.TariffGridService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class DictionariesControllerIT extends BaseControllerIT {
    @Autowired
    private TariffGridService tariffGridService;
    private final int tariffId = 3;

    @Test
    public void getCitiesByPostcode() {
        given().
                queryParam("postcode", "01015").
        when().
                get("/dictionaries/addresses").
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK).
                body("results", hasSize(greaterThan(0)));
    }
    
    @Test
    public void getAllTariffGrids() {
        when().
                get("/dictionaries/tariffs").
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK);
    }
    
    @Test
    public void getTariffGridById() {
        TariffGrid expectedTariff = tariffGridService.getEntityById(tariffId);
        
        when().
                get("/dictionaries/tariffs/{id}", tariffId).
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK).
                body("id", equalTo(tariffId)).
                body("price", equalTo(expectedTariff.getPrice()));
    }
    
    @Test
    public void getTariffGridByDimension() {
        TariffGrid expectedTariff = tariffGridService.getEntityById(tariffId);
        
        given().
                queryParam("weight", expectedTariff.getWeight()).
                queryParam("length", expectedTariff.getLength()).
                queryParam("w2wVariation", expectedTariff.getW2wVariation()).
        when().
                get("/dictionaries/tariffs").
        then().
                contentType(APPLICATION_JSON_VALUE).
                statusCode(SC_OK).
                body("id", equalTo(tariffId)).
                body("price", equalTo(expectedTariff.getPrice()));
    }
    
    @Test
    public void isPostcodeInCountryside_ok() {
        String countrysidePostcode = "07230";
        when().
                get("/dictionaries/countrysides/{postcode}", countrysidePostcode).
        then().
                statusCode(SC_OK);
    }
    
    @Test
    public void isPostcodeInCountryside_notFound() {
        String centralKyivPostcode = "01001";
        when().
                get("/dictionaries/countrysides/{postcode}", centralKyivPostcode).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
