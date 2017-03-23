package com.opinta.controller;

import java.util.List;

import com.opinta.dto.ClientDto;
import com.opinta.dto.VirtualPostOfficeDto;
import com.opinta.service.ClientService;
import com.opinta.service.VirtualPostOfficeService;
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
public class VirtualPostOfficeController {
    private final VirtualPostOfficeService postOfficeService;
    private final ClientService clientService;
    
    @Autowired
    public VirtualPostOfficeController(VirtualPostOfficeService postOfficeService, ClientService clientService) {
        this.postOfficeService = postOfficeService;
        this.clientService = clientService;
    }
    
    @GetMapping
    @ResponseStatus(OK)
    public List<VirtualPostOfficeDto> getAllPostOffices() {
        return postOfficeService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostOffice(@PathVariable("id") long id) {
        VirtualPostOfficeDto postOfficeDto = postOfficeService.getById(id);
        if (postOfficeDto == null) {
            return new ResponseEntity<>(format("No VirtualPostOffice found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(postOfficeDto, OK);
    }

    @GetMapping("{virtualPostOfficeId}/clients")
    public ResponseEntity<?> getClientsByVirtualPostOfficeId(@PathVariable long virtualPostOfficeId) {
        List<ClientDto> clientDtos = clientService.getAllByVirtualPostOfficeId(virtualPostOfficeId);
        if (clientDtos == null) {
            return new ResponseEntity<>(format("No VirtualPostOffice found for ID %d", virtualPostOfficeId), NOT_FOUND);
        }
        return new ResponseEntity<>(clientDtos, OK);
    }
    
    @PostMapping
    public ResponseEntity<?> createVirtualPostOffice(@RequestBody VirtualPostOfficeDto virtualPostOfficeDto) {
        virtualPostOfficeDto = postOfficeService.save(virtualPostOfficeDto);
        if (virtualPostOfficeDto == null) {
            return new ResponseEntity<>("New VirtualPostOffice has not been saved", BAD_REQUEST);
        }
        return new ResponseEntity<>(virtualPostOfficeDto, OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updatePostOfficeById(@PathVariable("id") long id,
                                               @RequestBody VirtualPostOfficeDto virtualPostOfficeDto) {
        virtualPostOfficeDto = postOfficeService.update(id, virtualPostOfficeDto);
        if (virtualPostOfficeDto == null) {
            return new ResponseEntity<>(format("No VirtualPostOffice found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(virtualPostOfficeDto, OK);
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostOfficeById(@PathVariable("id") long id) {
        if (!this.postOfficeService.delete(id)) {
            return new ResponseEntity<>(format("No VirtualPostOffice found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
