package com.opinta.controller;

import com.opinta.dto.CityDto;
import com.opinta.dto.classifier.TariffGridDto;
import com.opinta.entity.W2wVariation;
import com.opinta.entity.classifier.TariffGrid;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.service.CityService;
import com.opinta.service.CountrysidePostcodeService;
import com.opinta.service.TariffGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/dictionaries")
public class DictionariesController extends BaseController {
    private final TariffGridService tariffGridService;
    private final CountrysidePostcodeService countrysidePostcodeService;
    private final CityService cityService;
    
    @Autowired
    public DictionariesController(TariffGridService tariffGridService, CityService cityService,
            CountrysidePostcodeService countrysidePostcodeService) {
        this.tariffGridService  = tariffGridService;
        this.cityService = cityService;
        this.countrysidePostcodeService = countrysidePostcodeService;
    }
    
    @GetMapping(value = "tariffs", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllTariffGrids() {
        return new ResponseEntity<>(tariffGridService.getAll(), OK);
    }
    
    @GetMapping(value = "tariffs", produces = APPLICATION_JSON_VALUE, params = {"weight", "length", "w2wVariation"})
    public ResponseEntity<?> getTariffGridByDimensions(@RequestParam float weight, @RequestParam float length,
                                                       @RequestParam W2wVariation w2wVariation) {
        TariffGridDto tariff = tariffGridService.getByDimension(weight, length, w2wVariation);
        if (tariff != null) {
            return new ResponseEntity<>(tariff, OK);
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }
    
    @GetMapping(value = "tariffs/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTariffGridById(@PathVariable long id) {
        try {
            TariffGridDto tariff = tariffGridService.getById(id);
            return new ResponseEntity<>(tariff, OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(TariffGrid.class, id, e), NOT_FOUND);
        }
    }
    
    @GetMapping(value = "countrysides/{postcode}")
    public ResponseEntity<?> isPostcodeInCountryside(@PathVariable String postcode) {
        if (countrysidePostcodeService.isPostcodeInCountryside(postcode)) {
            return new ResponseEntity<>(OK);
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @RequestMapping("/addresses")
    public ResponseEntity<?> getAddressesByPostcode(@RequestParam String postcode) {
        return new ResponseEntity<>(cityService.getAllCitiesByPostcode(postcode), OK);
    }
}
