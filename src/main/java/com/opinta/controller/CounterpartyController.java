package com.opinta.controller;

import com.opinta.entity.User;
import com.opinta.service.UserService;
import java.util.List;

import com.opinta.dto.ClientDto;
import com.opinta.dto.CounterpartyDto;
import com.opinta.service.ClientService;
import com.opinta.service.CounterpartyService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@RestController
@RequestMapping("/counterparties")
public class CounterpartyController {
    private final CounterpartyService counterpartyService;
    private final ClientService clientService;
    private final UserService userService;
    
    @Autowired
    public CounterpartyController(CounterpartyService counterpartyService, ClientService clientService,
                                  UserService userService) {
        this.counterpartyService = counterpartyService;
        this.clientService = clientService;
        this.userService = userService;
    }
    
    @GetMapping
    @ResponseStatus(OK)
    public List<CounterpartyDto> getAllPostOffices() {
        return counterpartyService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostOffice(@PathVariable("id") long id) {
        CounterpartyDto counterpartyDto = counterpartyService.getById(id);
        if (counterpartyDto == null) {
            return new ResponseEntity<>(format("No Counterparty found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
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
        try {
            counterpartyDto = counterpartyService.save(counterpartyDto);
        } catch (Exception e) {
            return new ResponseEntity<>("New Counterparty has not been saved. " + e.getMessage(), BAD_REQUEST);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updatePostOfficeById(@PathVariable("id") long id,
                                                  @RequestBody CounterpartyDto counterpartyDto,
                                                  @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            counterpartyDto = counterpartyService.update(id, counterpartyDto, user);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostOfficeById(@PathVariable("id") long id,
                                                  @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            counterpartyService.delete(id, user);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
