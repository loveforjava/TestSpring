package com.opinta.controller;

import com.opinta.entity.Address;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;

import com.opinta.dto.AddressDto;
import com.opinta.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/addresses")
public class AddressController extends BaseController {
    private AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<AddressDto> getAddresses() {
        return addressService.getAll();
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
    public ResponseEntity<?> createAddress(@RequestBody @Valid AddressDto addressDto) {
        return new ResponseEntity<>(addressService.save(addressDto), OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateAddress(@PathVariable @Valid long id, @RequestBody AddressDto addressDto) {
        try {
            return new ResponseEntity<>(addressService.update(id, addressDto), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Address.class, addressDto, e), NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Address.class, addressDto, e), BAD_REQUEST);
        }
    }
}
