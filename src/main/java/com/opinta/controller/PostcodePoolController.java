package com.opinta.controller;

import java.util.List;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.service.BarcodeInnerNumberService;
import com.opinta.service.PostcodePoolService;
import java.util.UUID;
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

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/postcodes")
public class PostcodePoolController {
    private PostcodePoolService postcodePoolService;
    private BarcodeInnerNumberService barcodeInnerNumberService;

    @Autowired
    public PostcodePoolController(PostcodePoolService postcodePoolService,
                                  BarcodeInnerNumberService barcodeInnerNumberService) {
        this.postcodePoolService = postcodePoolService;
        this.barcodeInnerNumberService = barcodeInnerNumberService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<PostcodePoolDto> getPostcodePools() {
        return postcodePoolService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostcodePool(@PathVariable("id") UUID uuid) {
        PostcodePoolDto postcodePoolDto = postcodePoolService.getByUuid(uuid);
        if (postcodePoolDto == null) {
            return new ResponseEntity<>(format("No PostcodePool found for uuid %s", uuid), NOT_FOUND);
        }
        return new ResponseEntity<>(postcodePoolDto, OK);
    }

    @PostMapping
    @ResponseStatus(OK)
    public PostcodePoolDto createPostcodePool(@RequestBody PostcodePoolDto postcodePoolDto) {
        return postcodePoolService.save(postcodePoolDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePostcodePool(@PathVariable UUID uuid, @RequestBody PostcodePoolDto postcodePoolDto) {
        postcodePoolDto = postcodePoolService.update(uuid, postcodePoolDto);
        if (postcodePoolDto == null) {
            return new ResponseEntity<>(format("No PostcodePool found for uuid %s", uuid), NOT_FOUND);
        }
        return new ResponseEntity<>(postcodePoolDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostcodePool(@PathVariable UUID uuid) {
        if (!postcodePoolService.delete(uuid)) {
            return new ResponseEntity<>(format("No PostcodePool found for uuid %s", uuid), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @GetMapping("{postcodeId}/inner-numbers")
    public ResponseEntity<?> getBarcodeInnerNumbers(@PathVariable UUID postcodeUuid) {
        List<BarcodeInnerNumberDto> barcodeInnerNumberDtos = barcodeInnerNumberService.getAll(postcodeUuid);
        if (barcodeInnerNumberDtos == null) {
            return new ResponseEntity<>(format("PostcodePool %s doesn't exist", postcodeUuid), NOT_FOUND);
        }
        return new ResponseEntity<>(barcodeInnerNumberDtos, OK);
    }

    @GetMapping("inner-numbers/{id}")
    public ResponseEntity<?> getBarcodeInnerNumber(@PathVariable("id") long id) {
        BarcodeInnerNumberDto barcodeInnerNumberDto = barcodeInnerNumberService.getById(id);
        if (barcodeInnerNumberDto == null) {
            return new ResponseEntity<>(format("No barcodeInnerNumber found for ID %s", id), NOT_FOUND);
        }
        return new ResponseEntity<>(barcodeInnerNumberDto, OK);
    }

    @DeleteMapping("inner-numbers/{id}")
    public ResponseEntity<?> deleteBarcodeInnerNumber(@PathVariable long id) {
        if (!barcodeInnerNumberService.delete(id)) {
            return new ResponseEntity<>(format("No barcodeInnerNumber found for ID %s", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
