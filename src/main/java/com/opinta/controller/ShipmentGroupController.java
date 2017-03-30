package com.opinta.controller;

import com.opinta.dao.ShipmentGroupDao;
import com.opinta.dto.ClientDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.dto.ShipmentGroupDto;
import com.opinta.service.ClientService;
import com.opinta.service.ShipmentGroupService;
import com.opinta.service.ShipmentService;
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

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/shipment_groups")
public class ShipmentGroupController {
    private final ShipmentGroupService shipmentGroupService;
    private final ShipmentService shipmentService;

    @Autowired
    public ShipmentGroupController(ShipmentGroupService shipmentGroupService, ShipmentService shipmentService) {
        this.shipmentGroupService = shipmentGroupService;
        this.shipmentService = shipmentService;
    }
    
    @GetMapping
    @ResponseStatus(OK)
    public List<ShipmentGroupDto> getAllShipmentGroups() {
        return shipmentGroupService.getAll();
    }
    
    @GetMapping("{id}")
    public ResponseEntity<?> getShipmentGroup(@PathVariable("id") UUID uuid) {
        ShipmentGroupDto shipmentGroupDto = shipmentGroupService.getById(uuid);
        if (shipmentGroupDto == null) {
            return new ResponseEntity<>(format("No Client found for ID %s", uuid), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentGroupDto, OK);
    }

    @GetMapping("{id}/shipments")
    public ResponseEntity<?> getShipmentsByShipmentGroup(@PathVariable("id") UUID uuid) {
        // TODO
//        List<ShipmentDto> shipmentDtos = shipmentService.getAllByShipmentGroupId(uuid);
//        if (shipmentDtos == null) {
//            return new ResponseEntity<>(format("Client %s doesn't exist", uuid), NOT_FOUND);
//        }
//        return new ResponseEntity<>(shipmentDtos, OK);
        return new ResponseEntity<>(BAD_REQUEST);
    }
    
    @PostMapping
    public ResponseEntity<?> createShipmentGroup(@RequestBody ShipmentGroupDto shipmentGroupDto) {
        try {
            shipmentGroupDto = shipmentGroupService.save(shipmentGroupDto);
        } catch (Exception e) {
            return new ResponseEntity<>("New Client has not been saved", BAD_REQUEST);
        }
        return new ResponseEntity<>(shipmentGroupDto, OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updateShipmentGroup(@PathVariable("id") UUID uuid,
                                                 @RequestBody ShipmentGroupDto shipmentGroupDto) {
        try {
            shipmentGroupDto = shipmentGroupService.update(uuid, shipmentGroupDto);
        } catch (Exception e) {
            return new ResponseEntity<>(format("Error while updating %s. %s", uuid, e.getMessage()), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentGroupDto, OK);
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipmentGroup(@PathVariable("id") UUID uuid) {
        if (!shipmentGroupService.delete(uuid)) {
            return new ResponseEntity<>(format("No Client found for ID %s", uuid), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
