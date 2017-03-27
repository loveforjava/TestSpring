package com.opinta.controller;

import java.util.List;

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
@RequestMapping("/virtual-post-offices")
public class CounterpartyController {
    private final CounterpartyService postOfficeService;
    private final ClientService clientService;
    
    @Autowired
    public CounterpartyController(CounterpartyService postOfficeService, ClientService clientService) {
        this.postOfficeService = postOfficeService;
        this.clientService = clientService;
    }
    
    @GetMapping
    @ResponseStatus(OK)
    public List<CounterpartyDto> getAllPostOffices() {
        return postOfficeService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostOffice(@PathVariable("id") long id) {
        CounterpartyDto postOfficeDto = postOfficeService.getById(id);
        if (postOfficeDto == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(postOfficeDto, OK);
    }

    @GetMapping("{counterpartyId}/clients")
    public ResponseEntity<?> getClientsByCounterpartyId(@PathVariable long counterpartyId) {
        List<ClientDto> clientDtos = clientService.getAllByCounterpartyId(counterpartyId);
        if (clientDtos == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %d", counterpartyId), NOT_FOUND);
        }
        return new ResponseEntity<>(clientDtos, OK);
    }
    
    @PostMapping
    public ResponseEntity<?> createCounterparty(@RequestBody CounterpartyDto counterpartyDto) {
        counterpartyDto = postOfficeService.save(counterpartyDto);
        if (counterpartyDto == null) {
            return new ResponseEntity<>("New Counterparty has not been saved", BAD_REQUEST);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updatePostOfficeById(@PathVariable("id") long id,
                                               @RequestBody CounterpartyDto counterpartyDto) {
        counterpartyDto = postOfficeService.update(id, counterpartyDto);
        if (counterpartyDto == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostOfficeById(@PathVariable("id") long id) {
        if (!this.postOfficeService.delete(id)) {
            return new ResponseEntity<>(format("No Counterparty found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
