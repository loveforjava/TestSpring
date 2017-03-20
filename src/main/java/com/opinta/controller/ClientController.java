package com.opinta.controller;

import java.util.List;

import com.opinta.dto.ClientDto;
import com.opinta.service.ClientService;
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

import static com.pinta.util.ResponseEntityUtils.badRequest;
import static com.pinta.util.ResponseEntityUtils.notFound;
import static com.pinta.util.ResponseEntityUtils.ok;
import static com.pinta.util.ResponseEntityUtils.okWith;

/**
 * Created by Diarsid on 20.03.2017.
 */

@RestController
@RequestMapping("/clients")
public class ClientController {
    
    private final ClientService clientService;
    
    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }
    
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientDto> getAllClients() {
        return this.clientService.getAll();
    }
    
    @GetMapping("{id}")
    public ResponseEntity<?> getClientById(
            @PathVariable("id") long id) {
        ClientDto client = this.clientService.getById(id);
        if ( client != null ) {
            return okWith(client);
        } else {
            return ok();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createClient(
            @RequestBody ClientDto client) {
        boolean saved = this.clientService.save(client);
        if ( saved ) {
            return ok();
        } else {
            return badRequest("client not saved.");
        }
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updateClient(
            @PathVariable long id,
            @RequestBody ClientDto client) {
        ClientDto updatedClient = this.clientService.update(id, client);
        if ( updatedClient != null ) {
            return okWith(updatedClient);
        } else {
            return notFound(format("No Client found for ID %d", id));
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteClient(
            @PathVariable long id) {
        boolean removed = this.clientService.delete(id);
        if ( removed ) {
            return okWith(id);
        } else {
            return notFound(format("No Client found for ID %d", id));
        }
    }
    
}
