package com.opinta.controller;

import com.opinta.entity.User;
import com.opinta.service.UserService;
import java.util.List;
import java.util.UUID;

import com.opinta.dto.ClientDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.service.ClientService;
import com.opinta.service.ShipmentService;
import java.util.UUID;
import javax.naming.AuthenticationException;
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

import static java.lang.String.format;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;
    private final ShipmentService shipmentService;
    private final UserService userService;
    
    @Autowired
    public ClientController(ClientService clientService, ShipmentService shipmentService, UserService userService) {
        this.clientService = clientService;
        this.shipmentService = shipmentService;
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllClients(@RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(clientService.getAll(user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        }
    }
    
    @GetMapping("{id}")
    public ResponseEntity<?> getClient(@PathVariable("id") UUID id, @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(clientService.getByUuid(id, user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        }
    }

    @GetMapping("{id}/shipments")
    public ResponseEntity<?> getShipmentsByClientId(@PathVariable UUID id,
                                                    @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.getAllByClientUuid(id, user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(format("Client %s doesn't exist. " + e.getMessage(), id), UNAUTHORIZED);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto, @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(clientService.save(clientDto, user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("New Client has not been saved. " + e.getMessage(), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("New Client has not been saved. " + e.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateClient(@PathVariable UUID id, @RequestBody ClientDto clientDto,
                                          @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(clientService.update(id, clientDto, user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(format("Client %s has not been updated. ", id)
                    + ". " + e.getMessage(), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(format("Client %s has not been updated. ", id)
                    + ". " + e.getMessage(), NOT_FOUND);
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteClient(@PathVariable UUID id,
                                          @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            clientService.delete(id, user);
            return new ResponseEntity<>(OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(format("Client %s has not been deleted. ", id)
                    + ". " + e.getMessage(), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(format("Client %s has not been deleted. ", id)
                    + ". " + e.getMessage(), NOT_FOUND);
        }
    }
}
