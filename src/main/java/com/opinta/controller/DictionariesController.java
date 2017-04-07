package com.opinta.controller;

import com.opinta.dto.classifier.TariffGridDto;
import com.opinta.entity.W2wVariation;
import com.opinta.service.TariffGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/dictionaries")
public class DictionariesController {
    private final TariffGridService tariffGridService;
    
    @Autowired
    public DictionariesController(TariffGridService tariffGridService) {
        this.tariffGridService  = tariffGridService;
    }
    
    @GetMapping(value = "tariffs", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getAllTariffGrids() {
        return new ResponseEntity(tariffGridService.getAll(), OK);
    }
    
    @GetMapping(value = "tariffs", produces = APPLICATION_JSON_VALUE, params = {"weight", "length", "w2wVariation"})
    public ResponseEntity getTariffGridByDimensions(@RequestParam float weight,
                                                    @RequestParam float length,
                                                    @RequestParam W2wVariation w2wVariation) {
        TariffGridDto tariff = tariffGridService.getByDimension(weight, length, w2wVariation);
        if (tariff != null) {
            return new ResponseEntity(tariff, OK);
        } else {
            return new ResponseEntity(NOT_FOUND);
        }
    }
    
    @GetMapping(value = "tariffs/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getTariffGridById(@PathVariable long id) {
        TariffGridDto tariff = tariffGridService.getById(id);
        if (tariff != null) {
            return new ResponseEntity(tariff, OK);
        } else {
            return new ResponseEntity(NOT_FOUND);
        }
    }
}
