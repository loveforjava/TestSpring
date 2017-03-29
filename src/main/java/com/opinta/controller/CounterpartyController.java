package com.opinta.controller;

import java.util.List;
import java.util.UUID;

import com.opinta.dto.ClientDto;
import com.opinta.dto.CounterpartyDto;
import com.opinta.service.ClientService;
import com.opinta.service.CounterpartyService;
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
@RequestMapping("/counterparties")
public class CounterpartyController {
    private final CounterpartyService counterpartyService;
    private final ClientService clientService;
    
    @Autowired
    public CounterpartyController(CounterpartyService counterpartyService, ClientService clientService) {
        this.counterpartyService = counterpartyService;
        this.clientService = clientService;
    }
    
    @GetMapping
    @ResponseStatus(OK)
    public List<CounterpartyDto> getAllPostOffices() {
        return counterpartyService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostOffice(@PathVariable("id") UUID id) {
        CounterpartyDto counterpartyDto = counterpartyService.getById(id);
        if (counterpartyDto == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %s", id), NOT_FOUND);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }

    @GetMapping("{counterpartyId}/clients")
    public ResponseEntity<?> getClientsByCounterpartyId(@PathVariable UUID counterpartyId) {
        List<ClientDto> clientDtos = clientService.getAllByCounterpartyId(counterpartyId);
        if (clientDtos == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %s", counterpartyId), NOT_FOUND);
        }
        return new ResponseEntity<>(clientDtos, OK);
    }
    
    @PostMapping
    public ResponseEntity<?> createCounterparty(@RequestBody CounterpartyDto counterpartyDto) {
        counterpartyDto = counterpartyService.save(counterpartyDto);
        if (counterpartyDto == null) {
            return new ResponseEntity<>("New Counterparty has not been saved", BAD_REQUEST);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updatePostOfficeById(@PathVariable("id") UUID id,
                                                  @RequestBody CounterpartyDto counterpartyDto) {
        counterpartyDto = counterpartyService.update(id, counterpartyDto);
        if (counterpartyDto == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %s", id), NOT_FOUND);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostOfficeById(@PathVariable("id") UUID id) {
        if (!this.counterpartyService.delete(id)) {
            return new ResponseEntity<>(format("No Counterparty found for ID %s", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
