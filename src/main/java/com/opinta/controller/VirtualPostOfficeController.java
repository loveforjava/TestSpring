package com.opinta.controller;

import com.opinta.dto.VirtualPostOfficeDto;
import com.opinta.mapper.VirtualPostOfficeMapper;
import com.opinta.service.VirtualPostOfficeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Diarsid on 20.03.2017.
 */

@RestController
@RequestMapping("/virtual-post-offices")
public class VirtualPostOfficeController {
    
    private final VirtualPostOfficeService postOfficeService;
    private final VirtualPostOfficeMapper postOfficeMapper;
    
    public VirtualPostOfficeController(VirtualPostOfficeService postOfficeService,
            VirtualPostOfficeMapper postOfficeMapper) {
        this.postOfficeService = postOfficeService;
        this.postOfficeMapper = postOfficeMapper;
    }
    
    @GetMapping()
    public ResponseEntity getAllPostOffices() {
        return this.postOfficeService.getAll();
    }
    
    @PostMapping
    public ResponseEntity createVirtualPostoffice(
            @RequestBody VirtualPostOfficeDto dto) {
        
    }
    
    @GetMapping("{id}")
    public ResponseEntity getPostOfficeById(
            @PathVariable("id") long id) {
        
    }
    
    @PutMapping("{id}")
    public ResponseEntity updatePostOfficeById(
            @PathVariable("id") long id,
            @RequestBody VirtualPostOfficeDto dto) {
        
        
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity deletePostOfficeById(
            @PathVariable("id") long id) {
        
    }
}
