package com.opinta.controller;

import com.opinta.entity.User;
import com.opinta.service.UserService;
import java.util.List;

import com.opinta.dto.ShipmentDto;
import com.opinta.service.PDFGeneratorService;
import com.opinta.service.ShipmentService;
import java.util.UUID;
import javax.naming.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/shipments")
public class ShipmentController {
    private final ShipmentService shipmentService;
    private final PDFGeneratorService pdfGeneratorService;
    private final UserService userService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService, PDFGeneratorService pdfGeneratorService,
                              UserService userService) {
        this.shipmentService = shipmentService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getShipments(@RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.getAll(user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getShipment(@PathVariable("id") long id, @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.getById(id, user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        }
    }

    @GetMapping("{id}/label-form")
    public ResponseEntity<?> getShipmentLabelForm(@PathVariable("id") long id,
                                                  @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);

            byte[] data = pdfGeneratorService.generateLabel(id, user);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            String filename = "labelform" + id + ".pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(data, headers, OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        }
    }

    @GetMapping("{id}/postpay-form")
    public ResponseEntity<?> getShipmentPostpayForm(@PathVariable("id") long id,
                                                    @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);

            byte[] data = pdfGeneratorService.generatePostpay(id, user);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            String filename = "postpayform" + id + ".pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(data, headers, OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<?> createShipment(@RequestBody ShipmentDto shipmentDto,
                                            @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.save(shipmentDto, user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("New Shipment has not been saved. " + e.getMessage(), UNAUTHORIZED);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateShipment(@PathVariable long id, @RequestBody ShipmentDto shipmentDto,
                                            @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.update(id, shipmentDto, user), OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Shipment has not been updated. " + e.getMessage(), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Shipment has not been updated. " + e.getMessage(), NOT_FOUND);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipment(@PathVariable long id,
                                            @RequestParam(value = "token") UUID token) {
        try {
            User user = userService.authenticate(token);
            shipmentService.delete(id, user);
            return new ResponseEntity<>(OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Shipment has not been deleted. " + e.getMessage(), NOT_FOUND);
        }
    }
}
