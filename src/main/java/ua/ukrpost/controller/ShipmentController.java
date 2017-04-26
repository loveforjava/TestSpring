package ua.ukrpost.controller;

import ua.ukrpost.entity.Shipment;
import ua.ukrpost.entity.ShipmentGroup;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import ua.ukrpost.service.UserService;
import java.io.IOException;
import java.util.UUID;

import ua.ukrpost.dto.ShipmentDto;
import ua.ukrpost.service.PDFGeneratorService;
import ua.ukrpost.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import ua.ukrpost.util.LogMessageUtil;

import javax.validation.Valid;

import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.saveOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateOnErrorLogEndpoint;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestController
@RequestMapping("/shipments")
public class ShipmentController extends BaseController {
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
    public ResponseEntity<?> getShipments(@RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.getAll(user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(e.getMessage(), UNAUTHORIZED);
        }
    }

    @GetMapping("{uuid}")
    public ResponseEntity<?> getShipment(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.getByUuid(uuid, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(LogMessageUtil.getByIdOnErrorLogEndpoint(Shipment.class, uuid, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(LogMessageUtil.getByIdOnErrorLogEndpoint(Shipment.class, uuid, e), NOT_FOUND);
        }
    }

    @GetMapping("{uuid}/form")
    public ResponseEntity<?> getShipmentForm(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);

            byte[] data = pdfGeneratorService.generateShipmentForm(uuid, user);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(parseMediaType(APPLICATION_PDF_VALUE));
            String filename = uuid + ".pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(data, headers, OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(LogMessageUtil.generatePdfFormOnErrorLogEndpoint(Shipment.class, uuid, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(LogMessageUtil.generatePdfFormOnErrorLogEndpoint(Shipment.class, uuid, e), NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(LogMessageUtil.generatePdfFormOnErrorLogEndpoint(Shipment.class, uuid, e), BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> createShipment(@RequestBody @Valid ShipmentDto shipmentDto, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.save(shipmentDto, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(LogMessageUtil.saveOnErrorLogEndpoint(Shipment.class, shipmentDto, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(LogMessageUtil.saveOnErrorLogEndpoint(Shipment.class, shipmentDto, e), NOT_FOUND);
        }
    }

    @PutMapping("{uuid}")
    public ResponseEntity<?> updateShipment(@PathVariable UUID uuid, @RequestBody @Valid ShipmentDto shipmentDto,
                                            @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.update(uuid, shipmentDto, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(LogMessageUtil.updateOnErrorLogEndpoint(Shipment.class, uuid, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(LogMessageUtil.updateOnErrorLogEndpoint(Shipment.class, uuid, e), NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(LogMessageUtil.updateOnErrorLogEndpoint(Shipment.class, uuid, e), BAD_REQUEST);
        }
    }

    @DeleteMapping("{uuid}/shipment-group")
    public ResponseEntity<?> removeShipmentGroupFromShipment(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentService.removeShipmentGroupFromShipment(uuid, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(LogMessageUtil.deleteFieldOnErrorLogEndpoint(Shipment.class, ShipmentGroup.class,
                    uuid, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(LogMessageUtil.deleteFieldOnErrorLogEndpoint(Shipment.class, ShipmentGroup.class,
                    uuid, e), UNAUTHORIZED);
        }
    }
}
