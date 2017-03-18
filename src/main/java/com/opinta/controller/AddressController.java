package com.opinta.controller;

import com.opinta.dto.AddressDto;
import com.opinta.model.Address;
import com.opinta.model.Client;
import com.opinta.model.Customer;
import com.opinta.service.AddressService;
import com.opinta.service.CustomerService;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<AddressDto> getAddresses() {
        return addressService.getAll();
    }

//	@GetMapping("/customers/{id}")
//	public ResponseEntity<?> getCustomer(@PathVariable("id") Long id) {
//
//		Customer customer = customerService.getById(id);
//		if (customer == null) {
//			return new ResponseEntity<>("No Customer found for ID " + id, HttpStatus.NOT_FOUND);
//		}
//
//		return new ResponseEntity<>(customer, HttpStatus.OK);
//	}
//
//	@PostMapping(value = "/customers")
//	public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
//
//		customerService.save(customer);
//
//		return new ResponseEntity<>(customer, HttpStatus.OK);
//	}
//
//	@PutMapping("/customers/{id}")
//	public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
//
//		customer = customerService.update(id, customer);
//
//		if (null == customer) {
//			return new ResponseEntity<>("No Customer found for ID " + id, HttpStatus.NOT_FOUND);
//		}
//
//		return new ResponseEntity<>(customer, HttpStatus.OK);
//	}
//
//    @DeleteMapping("/customers/{id}")
//    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
//
//        if (!customerService.delete(id)) {
//            return new ResponseEntity("No Customer found for ID " + id, HttpStatus.NOT_FOUND);
//        }
//
//        return new ResponseEntity(id, HttpStatus.OK);
//    }
}