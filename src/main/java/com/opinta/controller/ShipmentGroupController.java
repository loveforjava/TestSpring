package com.opinta.controller;

import com.opinta.dto.ShipmentDto;
import com.opinta.dto.ShipmentGroupDto;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.service.PDFGeneratorService;
import com.opinta.service.ShipmentGroupService;
import com.opinta.service.ShipmentService;
import com.opinta.service.UserService;
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

import java.io.IOException;
import java.util.List;
import java.util.UUID;


import static com.opinta.util.LogMessageUtil.generatePdfFormOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteOnErrorLogEndpoint;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.parseMediaType;

@RestController
@RequestMapping("/shipment-groups")
public class ShipmentGroupController {
    private final ShipmentGroupService shipmentGroupService;
    private final ShipmentService shipmentService;
    private final UserService userService;
    private final PDFGeneratorService pdfGeneratorService;

    @Autowired
    public ShipmentGroupController(ShipmentGroupService shipmentGroupService, ShipmentService shipmentService,
                                   UserService userService, PDFGeneratorService pdfGeneratorService) {
        this.shipmentGroupService = shipmentGroupService;
        this.shipmentService = shipmentService;
        this.userService = userService;
        this.pdfGeneratorService = pdfGeneratorService;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllShipmentGroups(@RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(shipmentGroupService.getAll(user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(getAllOnErrorLogEndpoint(ShipmentGroup.class), UNAUTHORIZED);
        }
    }

    @GetMapping("{uuid}/shipments")
    public ResponseEntity<?> getShipmentsByShipmentGroupUuid(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            List<ShipmentDto> shipmentDtos = shipmentService.getAllByShipmentGroupUuid(uuid, user);
            return new ResponseEntity<>(shipmentDtos, OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(ShipmentGroup.class, uuid), NOT_FOUND);
        } catch (AuthException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(ShipmentGroup.class, uuid), UNAUTHORIZED);
        }
    }

    @GetMapping("{uuid}/form")
    public ResponseEntity<?> getShipmentGroupForm(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);

            byte[] data = pdfGeneratorService.generateShipmentGroupForms(uuid, user);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(parseMediaType(APPLICATION_PDF_VALUE));
            String filename = uuid + ".pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(data, headers, OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(generatePdfFormOnErrorLogEndpoint(ShipmentGroup.class, uuid, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(generatePdfFormOnErrorLogEndpoint(ShipmentGroup.class, uuid, e), NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(generatePdfFormOnErrorLogEndpoint(ShipmentGroup.class, uuid, e), BAD_REQUEST);
        }
    }

    @GetMapping("{uuid}/form103")
    public ResponseEntity<?> getShipmentGroupForm103(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            byte[] data = pdfGeneratorService.generateForm103(uuid, user);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(parseMediaType(APPLICATION_PDF_VALUE));
            String filename = uuid + ".pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(data, headers, OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(generatePdfFormOnErrorLogEndpoint(ShipmentGroup.class, uuid, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(generatePdfFormOnErrorLogEndpoint(ShipmentGroup.class, uuid, e), NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(generatePdfFormOnErrorLogEndpoint(ShipmentGroup.class, uuid, e), BAD_REQUEST);
        }
    }

    @GetMapping("{uuid}")
    public ResponseEntity<?> getShipmentGroup(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            ShipmentGroupDto shipmentGroupDto = shipmentGroupService.getById(uuid, user);
            return new ResponseEntity<>(shipmentGroupDto, OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(ShipmentGroup.class, uuid), NOT_FOUND);
        } catch (AuthException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(ShipmentGroup.class, uuid), UNAUTHORIZED);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createShipmentGroup(@RequestBody ShipmentGroupDto shipmentGroupDto,
                                                 @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            shipmentGroupDto = shipmentGroupService.save(shipmentGroupDto, user);
            return new ResponseEntity<>(shipmentGroupDto, OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(saveOnErrorLogEndpoint(ShipmentGroup.class, shipmentGroupDto), NOT_FOUND);
        } catch (AuthException e) {
            return new ResponseEntity<>(saveOnErrorLogEndpoint(ShipmentGroup.class, shipmentGroupDto), UNAUTHORIZED);
        }
    }
    
    @PutMapping("{uuid}")
    public ResponseEntity<?> updateShipmentGroup(@PathVariable UUID uuid,
                                                 @RequestBody ShipmentGroupDto shipmentGroupDto,
                                                 @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            shipmentGroupDto = shipmentGroupService.update(uuid, shipmentGroupDto, user);
            return new ResponseEntity<>(shipmentGroupDto, OK);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(ShipmentGroup.class, uuid), BAD_REQUEST);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(ShipmentGroup.class, uuid), NOT_FOUND);
        } catch (AuthException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(ShipmentGroup.class, uuid), UNAUTHORIZED);
        }
    }
    
    @DeleteMapping("{uuid}")
    public ResponseEntity<?> deleteShipmentGroup(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            shipmentGroupService.delete(uuid, user);
            return new ResponseEntity<>(OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(deleteOnErrorLogEndpoint(ShipmentGroup.class, uuid), NOT_FOUND);
        } catch (AuthException e) {
            return new ResponseEntity<>(deleteOnErrorLogEndpoint(ShipmentGroup.class, uuid), UNAUTHORIZED);
        }
    }
}
