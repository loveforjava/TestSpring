package com.opinta.controller;

import com.opinta.dao.CityDao;
import com.opinta.entity.Address;
import com.opinta.entity.classifier.City;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;

import com.opinta.dto.AddressDto;
import com.opinta.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.opinta.util.LogMessageUtil.deleteOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private AddressService addressService;

    @Autowired
    private CityDao cityDao;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<AddressDto> getAddresses() {
        return addressService.getAll();
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        List<City> cities = cityDao.getAllCitiesByPostcode("01014");
        System.out.println(cities);
        return null;
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAddress(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(addressService.getById(id), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(Address.class, id, e), NOT_FOUND);
        }
    }

    @PostMapping
    @ResponseStatus(OK)
    public ResponseEntity<?> createAddress(@RequestBody AddressDto addressDto) {
        return new ResponseEntity<>(addressService.save(addressDto), OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateAddress(@PathVariable long id, @RequestBody AddressDto addressDto) {
        try {
            return new ResponseEntity<>(addressService.update(id, addressDto), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Address.class, addressDto, e), NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Address.class, addressDto, e), BAD_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable long id) {
        try {
            addressService.delete(id);
            return new ResponseEntity<>(OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(deleteOnErrorLogEndpoint(Address.class, id, e), NOT_FOUND);
        }
    }
}
