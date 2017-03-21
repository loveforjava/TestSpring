package com.opinta.controller;

import com.opinta.dto.AddressDto;
import com.opinta.service.AddressService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import static java.lang.String.format;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AddressDto> getAddresses() {
        return addressService.getAll();
    }

	@GetMapping("{id}")
	public ResponseEntity<?> getAddress(@PathVariable("id") long id) {
		AddressDto addressDto = addressService.getById(id);
		if (addressDto == null) {
			return new ResponseEntity<>(format("No Address found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(addressDto, HttpStatus.OK);
	}

	@PostMapping
    @ResponseStatus(HttpStatus.OK)
	public void createAddress(@RequestBody AddressDto addressDto) {
		addressService.save(addressDto);
	}

	@PutMapping("{id}")
	public ResponseEntity<?> updateAddress(@PathVariable long id, @RequestBody AddressDto addressDto) {
		addressDto = addressService.update(id, addressDto);
		if (addressDto == null) {
			return new ResponseEntity<>(format("No Address found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(addressDto, HttpStatus.OK);
	}

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable long id) {
        if (!addressService.delete(id)) {
            return new ResponseEntity<>(format("No Address found for ID %d", id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
