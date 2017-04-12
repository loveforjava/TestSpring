package com.opinta.controller;

import java.util.UUID;

import com.opinta.dto.DiscountDto;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/discounts")
public class DiscountController {
    private final DiscountService discountService;
    
    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllDiscounts() {
        return new ResponseEntity<>(discountService.getAllEntities(), OK);
    }
    
    @GetMapping("{id}")
    public ResponseEntity<?> getDiscountByUuid(@PathVariable UUID uuid) {
        try {
            return new ResponseEntity<>(discountService.getEntityByUuid(uuid), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createDiscount(DiscountDto discountDto) {
        return new ResponseEntity<>(discountService.save(discountDto), OK);
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteDiscount(@PathVariable UUID uuid) {
        try {
            discountService.deleteByUuid(uuid);
            return new ResponseEntity<>(OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
        }
    }
}
