package com.opinta.controller;

import java.util.List;

import com.opinta.dto.VirtualPostOfficeDto;
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
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/virtual-post-offices")
public class VirtualPostOfficeController {
    
    private final VirtualPostOfficeService postOfficeService;
    
    @Autowired
    public VirtualPostOfficeController(VirtualPostOfficeService postOfficeService) {
        this.postOfficeService = postOfficeService;
    }
    
    @GetMapping()
    public List<VirtualPostOfficeDto> getAllPostOffices() {
        return this.postOfficeService.getAll();
    }
    
    @PostMapping
    public ResponseEntity createVirtualPostOffice(@RequestBody VirtualPostOfficeDto dto) {
        dto = this.postOfficeService.save(dto);
        if (dto != null) {
            return new ResponseEntity(dto, OK);
        } else {
            return new ResponseEntity("new VirtualPostOffice has not been saved.", BAD_REQUEST);
        }
    }
    
    @GetMapping("{id}")
    public ResponseEntity getPostOfficeById(@PathVariable("id") long id) {
        VirtualPostOfficeDto postOfficeDto = this.postOfficeService.getById(id);
        if (postOfficeDto != null) {
            return new ResponseEntity(postOfficeDto, OK);
        } else {
            return new ResponseEntity(format("VirtualPostOffice not found by id %d", id), NOT_FOUND);
        }
    }
    
    @PutMapping("{id}")
    public ResponseEntity updatePostOfficeById(
            @PathVariable("id") long id,
            @RequestBody VirtualPostOfficeDto dto) {
        dto = this.postOfficeService.update(id, dto);
        if (dto != null) {
            return new ResponseEntity(dto, OK);
        } else {
            return new ResponseEntity(format("VirtualPostOffice not updated by id %d", id), BAD_REQUEST);
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity deletePostOfficeById(@PathVariable("id") long id) {
        boolean removed = this.postOfficeService.delete(id);
        if (removed) {
            return new ResponseEntity(OK);
        } else {
            return new ResponseEntity(format("VirtualPostOffice not removed by id %d", id), BAD_REQUEST);
        }
    }
}
