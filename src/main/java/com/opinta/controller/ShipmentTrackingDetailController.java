package com.opinta.controller;

import com.opinta.entity.ShipmentTrackingDetail;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.util.LogMessageUtil;
import java.util.List;

import com.opinta.dto.ShipmentTrackingDetailDto;
import com.opinta.service.ShipmentTrackingDetailService;
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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/shipment-tracking")
public class ShipmentTrackingDetailController {
    private ShipmentTrackingDetailService shipmentTrackingDetailService;

    @Autowired
    public ShipmentTrackingDetailController(ShipmentTrackingDetailService shipmentTrackingDetailService) {
        this.shipmentTrackingDetailService = shipmentTrackingDetailService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<ShipmentTrackingDetailDto> getShipmentTrackingDetails() {
        return shipmentTrackingDetailService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getShipmentTrackingDetail(@PathVariable long id) {
        try {
            return new ResponseEntity<>(shipmentTrackingDetailService.getById(id), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(LogMessageUtil.getByIdOnErrorLogEndpoint(ShipmentTrackingDetail.class, id),
                    NOT_FOUND);
        }
    }

    @PostMapping
    @ResponseStatus(OK)
    public void createShipmentTrackingDetail(@RequestBody ShipmentTrackingDetailDto shipmentTrackingDetailDto) {
        shipmentTrackingDetailService.save(shipmentTrackingDetailDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateShipmentTrackingDetail(
            @PathVariable long id, @RequestBody ShipmentTrackingDetailDto shipmentTrackingDetailDto) {
        try {
            return new ResponseEntity<>(shipmentTrackingDetailService.update(id, shipmentTrackingDetailDto), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(LogMessageUtil.getByIdOnErrorLogEndpoint(ShipmentTrackingDetail.class, id),
                    NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(LogMessageUtil.getByIdOnErrorLogEndpoint(ShipmentTrackingDetail.class, id),
                    BAD_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipmentTrackingDetail(@PathVariable long id) {
        try {
            shipmentTrackingDetailService.delete(id);
            return new ResponseEntity<>(OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(LogMessageUtil.getByIdOnErrorLogEndpoint(ShipmentTrackingDetail.class, id),
                    NOT_FOUND);
        }
    }
}
