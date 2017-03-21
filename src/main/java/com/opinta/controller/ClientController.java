package com.opinta.controller;

import java.util.List;

import com.opinta.dto.ClientDto;
import com.opinta.service.ClientService;
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
public class ClientController {
    
    private final ClientService clientService;
    
    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }
    
    @GetMapping
    @ResponseStatus(OK)
    public List<ClientDto> getAllClients() {
        return this.clientService.getAll();
    }
    
    @GetMapping("{id}")
    public ResponseEntity getClientById(@PathVariable("id") long id) {
        ClientDto client = this.clientService.getById(id);
        if (client != null) {
            return new ResponseEntity(client, OK);
        } else {
            return new ResponseEntity(NOT_FOUND);
        }
    }
    
    @PostMapping
    public ResponseEntity createClient(@RequestBody ClientDto client) {
        ClientDto saved = this.clientService.save(client);
        if (saved != null) {
            return new ResponseEntity(saved, OK);
        } else {
            return new ResponseEntity("client not saved.", BAD_REQUEST);
        }
    }
    
    @PutMapping("{id}")
    public ResponseEntity updateClient(
            @PathVariable long id,
            @RequestBody ClientDto client) {
        ClientDto updatedClient = this.clientService.update(id, client);
        if (updatedClient != null) {
            return new ResponseEntity(updatedClient, OK);
        } else {
            return new ResponseEntity(format("No Client found for ID %d", id), NOT_FOUND);
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity deleteClient(@PathVariable long id) {
        boolean removed = this.clientService.delete(id);
        if (removed) {
            return new ResponseEntity(id, OK);
        } else {
            return new ResponseEntity(format("No Client found for ID %d", id), NOT_FOUND);
        }
    }
    
}
