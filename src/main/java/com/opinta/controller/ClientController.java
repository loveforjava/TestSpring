package com.opinta.controller;

import com.opinta.entity.Client;
import com.opinta.entity.Shipment;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.service.UserService;
import java.util.UUID;

import com.opinta.dto.ClientDto;
import com.opinta.service.ClientService;
import com.opinta.service.ShipmentService;
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

import javax.validation.Valid;

import static com.opinta.util.LogMessageUtil.deleteOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByFieldOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;

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
    public ResponseEntity<?> getAllClients(@RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(clientService.getAll(user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(getAllOnErrorLogEndpoint(Client.class, e), UNAUTHORIZED);
        }
    }

    @GetMapping("{uuid}")
    public ResponseEntity<?> getClient(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(clientService.getByUuid(uuid, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(Client.class, uuid, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(Client.class, uuid, e), NOT_FOUND);
        }
    }

    @GetMapping("{uuid}/shipments")
    public ResponseEntity<?> getShipmentsByClientId(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.getAllByClientUuid(uuid, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(getByFieldOnErrorLogEndpoint(Shipment.class, Client.class, uuid, e),
                    UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByFieldOnErrorLogEndpoint(Shipment.class, Client.class, uuid, e),
                    NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody @Valid ClientDto clientDto, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(clientService.save(clientDto, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(saveOnErrorLogEndpoint(Client.class, clientDto, e), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(saveOnErrorLogEndpoint(Client.class, clientDto, e), BAD_REQUEST);
        }
    }

    @PutMapping("{uuid}")
    public ResponseEntity<?> updateClient(@PathVariable UUID uuid, @RequestBody @Valid ClientDto clientDto,
                                          @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(clientService.update(uuid, clientDto, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Client.class, clientDto, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Client.class, clientDto, e), NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Client.class, clientDto, e), BAD_REQUEST);
        }
    }

    @DeleteMapping("{uuid}")
    public ResponseEntity<?> deleteClient(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            clientService.delete(uuid, user);
            return new ResponseEntity<>(OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(deleteOnErrorLogEndpoint(Client.class, uuid, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(deleteOnErrorLogEndpoint(Client.class, uuid, e), NOT_FOUND);
        }
    }
}
