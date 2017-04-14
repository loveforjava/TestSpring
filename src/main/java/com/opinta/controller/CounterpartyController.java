package com.opinta.controller;

import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.service.UserService;
import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.opinta.util.LogMessageUtil.deleteOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;

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
    public ResponseEntity<?> getCounterparty(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(counterpartyService.getByUuid(uuid, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(Counterparty.class, uuid, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(Counterparty.class, uuid, e), NOT_FOUND);
        }
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCounterparty(@RequestBody CounterpartyDto counterpartyDto) {
        try {
            return new ResponseEntity<>(counterpartyService.save(counterpartyDto), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(saveOnErrorLogEndpoint(Counterparty.class, counterpartyDto, e), BAD_REQUEST);
        }
    }

    @PutMapping(value = "{uuid}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCounterpartyByUuid(@PathVariable UUID uuid,
                                                      @RequestBody CounterpartyDto counterpartyDto,
                                                      @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(counterpartyService.update(uuid, counterpartyDto, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Counterparty.class, uuid, e), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Counterparty.class, uuid, e), NOT_FOUND);
        }
    }

    @DeleteMapping("{uuid}")
    public ResponseEntity<?> deletePostOfficeById(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            counterpartyService.delete(uuid, user);
            return new ResponseEntity<>(OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(deleteOnErrorLogEndpoint(Counterparty.class, uuid, e), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(deleteOnErrorLogEndpoint(Counterparty.class, uuid, e), NOT_FOUND);
        }
    }
}
