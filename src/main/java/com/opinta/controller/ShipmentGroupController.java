package com.opinta.controller;

import com.opinta.dto.ShipmentDto;
import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.service.ShipmentGroupService;
import com.opinta.service.ShipmentService;
import com.opinta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.UUID;

import static com.opinta.util.LogMessageUtil.deleteOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/shipment-groups")
public class ShipmentGroupController {
    private final ShipmentGroupService shipmentGroupService;
    private final ShipmentService shipmentService;
    private final UserService userService;

    @Autowired
    public ShipmentGroupController(ShipmentGroupService shipmentGroupService, ShipmentService shipmentService,
                                   UserService userService) {
        this.shipmentGroupService = shipmentGroupService;
        this.shipmentService = shipmentService;
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<?>  getAllShipmentGroups(@RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentGroupService.getAll(user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        }
    }
    
    @GetMapping("{id}")
    public ResponseEntity<?> getShipmentGroup(@PathVariable("id") UUID uuid,
                                              @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            ShipmentGroupDto shipmentGroupDto = shipmentGroupService.getById(uuid, user);
            return new ResponseEntity<>(shipmentGroupDto, OK);
        } catch (Exception e) {
            return new ResponseEntity<>(getOnErrorLogEndpoint(ShipmentGroup.class, uuid), NOT_FOUND);
        }
    }

    @GetMapping("{id}/shipments")
    public ResponseEntity<?> getShipmentsByShipmentGroup(@PathVariable("id") UUID uuid,
                                                         @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            List<ShipmentDto> shipmentDtos = shipmentService.getAllByShipmentGroupId(uuid, user);
            return new ResponseEntity<>(shipmentDtos, OK);
        } catch (Exception e) {
            return new ResponseEntity<>(getOnErrorLogEndpoint(ShipmentGroup.class, uuid), NOT_FOUND);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createShipmentGroup(@RequestBody ShipmentGroupDto shipmentGroupDto,
                                                 @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            shipmentGroupDto = shipmentGroupService.save(shipmentGroupDto, user);
            return new ResponseEntity<>(shipmentGroupDto, OK);
        } catch (Exception e) {
            return new ResponseEntity<>(saveOnErrorLogEndpoint(ShipmentGroup.class, shipmentGroupDto), BAD_REQUEST);
        }
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updateShipmentGroup(@PathVariable("id") UUID uuid,
                                                 @RequestBody ShipmentGroupDto shipmentGroupDto,
                                                 @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            shipmentGroupDto = shipmentGroupService.update(uuid, shipmentGroupDto, user);
            return new ResponseEntity<>(shipmentGroupDto, OK);
        } catch (Exception e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(ShipmentGroup.class, uuid), NOT_FOUND);
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipmentGroup(@PathVariable("id") UUID uuid,
                                                 @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            shipmentGroupService.delete(uuid, user);
            return new ResponseEntity<>(OK);
        } catch (Exception e) {
            return new ResponseEntity<>(deleteOnErrorLogEndpoint(ShipmentGroup.class, uuid), NOT_FOUND);
        }
    }
}
