package com.opinta.controller;

import java.util.List;
import java.util.UUID;

import com.opinta.dto.ClientDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.service.ClientService;
import com.opinta.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
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

import static java.lang.String.format;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/clients")
@Slf4j
public class ClientController {
    private final ClientService clientService;
    private final ShipmentService shipmentService;
    
    @Autowired
    public ClientController(ClientService clientService, ShipmentService shipmentService) {
        this.clientService = clientService;
        this.shipmentService = shipmentService;
    }
    
    @GetMapping
    @ResponseStatus(OK)
    public List<ClientDto> getAllClients() {
        return this.clientService.getAll();
    }
    
    @GetMapping("{id}")
    public ResponseEntity<?> getClient(@PathVariable("id") UUID id) {
        ClientDto clientDto = clientService.getById(id);
        if (clientDto == null) {
            return new ResponseEntity<>(format("No Client found for ID %s", id), NOT_FOUND);
        }
        return new ResponseEntity<>(clientDto, OK);
    }

    @GetMapping("{clientId}/shipments")
    public ResponseEntity<?> getShipmentsByClientId(@PathVariable UUID clientId) {
        List<ShipmentDto> shipmentDtos = shipmentService.getAllByClientId(clientId);
        if (shipmentDtos == null) {
            return new ResponseEntity<>(format("Client %s doesn't exist", clientId), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDtos, OK);
    }
    
    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto) {
        try {
            clientDto = clientService.save(clientDto);
        } catch (Exception e) {
            return new ResponseEntity<>("New Client has not been saved", BAD_REQUEST);
        }
        return new ResponseEntity<>(clientDto, OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updateClient(@PathVariable UUID id, @RequestBody ClientDto clientDto) {
        try {
            clientDto = clientService.update(id, clientDto);
        } catch (Exception e) {
            return new ResponseEntity<>(format("Error while updating %s", id) + ". " + e.getMessage(), NOT_FOUND);
        }
        return new ResponseEntity<>(clientDto, OK);
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteClient(@PathVariable UUID id) {
        if (!clientService.delete(id)) {
            return new ResponseEntity<>(format("No Client found for ID %s", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
