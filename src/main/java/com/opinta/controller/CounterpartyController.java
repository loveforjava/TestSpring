package com.opinta.controller;

import com.opinta.entity.User;
import com.opinta.service.UserService;
import java.util.List;
import java.util.UUID;

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
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


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
    public List<CounterpartyDto> getAllCounterparties() {
        return counterpartyService.getAll();
    }

    @GetMapping(value = "{uuid}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCounterparty(@PathVariable("uuid") UUID uuid) {
        CounterpartyDto counterpartyDto = counterpartyService.getByUuid(uuid);
        if (counterpartyDto == null) {
            return new ResponseEntity<>(format("No Counterparty found for uuid %s", uuid), NOT_FOUND);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }

    @GetMapping(value = "{counterpartyUuid}/clients", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getClientsByCounterpartyId(@PathVariable UUID counterpartyUuid) {
        List<ClientDto> clientDtos = clientService.getAllByCounterpartyUuid(counterpartyUuid);
        if (clientDtos == null) {
            return new ResponseEntity<>(format("No Counterparty found for uuid %s", counterpartyUuid), NOT_FOUND);
        }
        return new ResponseEntity<>(clientDtos, OK);
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCounterparty(@RequestBody CounterpartyDto counterpartyDto) {
        try {
            counterpartyDto = counterpartyService.save(counterpartyDto);
        } catch (Exception e) {
            return new ResponseEntity<>(format("New Counterparty has not been saved. %s", e.getMessage()), BAD_REQUEST);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }

    @PutMapping(value = "{uuid}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCounterpartyByUuid(@PathVariable("uuid") UUID uuid,
                                                  @RequestBody CounterpartyDto counterpartyDto,
                                                  @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            counterpartyDto = counterpartyService.update(uuid, counterpartyDto, user);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(format("New Counterparty has not been updated. %s", e.getMessage()), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(format("New Counterparty has not been updated. %s", e.getMessage()), NOT_FOUND);
        }
        return new ResponseEntity<>(counterpartyDto, OK);
    }

    @DeleteMapping("{uuid}")
    public ResponseEntity<?> deletePostOfficeById(@PathVariable("uuid") UUID uuid,
                                                  @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            counterpartyService.delete(uuid, user);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
