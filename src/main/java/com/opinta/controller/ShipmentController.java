package com.opinta.controller;

import com.opinta.dto.ShipmentDto;
import com.opinta.service.ShipmentService;
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

import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {
	private ShipmentService shipmentService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService) {
    	this.shipmentService = shipmentService;
	}

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShipmentDto> getShipments() {
        return shipmentService.getAll();
    }

	@GetMapping("{id}")
	public ResponseEntity<?> getShipment(@PathVariable("id") Long id) {
		ShipmentDto shipmentDto = shipmentService.getById(id);
		if (shipmentDto == null) {
			return new ResponseEntity<>(format("No Shipment found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(shipmentDto, HttpStatus.OK);
	}

	@PostMapping
    @ResponseStatus(HttpStatus.OK)
	public ShipmentDto createShipment(@RequestBody ShipmentDto shipmentDto) {
		return shipmentService.save(shipmentDto);
	}

	@PutMapping("{id}")
	public ResponseEntity<?> updateShipment(@PathVariable Long id, @RequestBody ShipmentDto shipmentDto) {
		shipmentDto = shipmentService.update(id, shipmentDto);
		if (shipmentDto == null) {
			return new ResponseEntity<>(format("No Shipment found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(shipmentDto, HttpStatus.OK);
	}

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipment(@PathVariable Long id) {
        if (!shipmentService.delete(id)) {
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
