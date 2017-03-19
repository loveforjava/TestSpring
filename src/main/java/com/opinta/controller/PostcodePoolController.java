package com.opinta.controller;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.service.BarcodeInnerNumberService;
import com.opinta.service.PostcodePoolService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.OK)
    public List<PostcodePoolDto> getPostcodePools() {
        return postcodePoolService.getAll();
    }

	@GetMapping("{id}")
	public ResponseEntity<?> getPostcodePool(@PathVariable("id") Long id) {
		PostcodePoolDto postcodePoolDto = postcodePoolService.getById(id);
		if (postcodePoolDto == null) {
			return new ResponseEntity<>(format("No PostcodePool found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(postcodePoolDto, HttpStatus.OK);
	}

	@PostMapping
    @ResponseStatus(HttpStatus.OK)
	public void createPostcodePool(@RequestBody PostcodePoolDto postcodePoolDto) {
		postcodePoolService.save(postcodePoolDto);
	}

	@PutMapping("{id}")
	public ResponseEntity<?> updatePostcodePool(@PathVariable Long id, @RequestBody PostcodePoolDto postcodePoolDto) {
        postcodePoolDto = postcodePoolService.update(id, postcodePoolDto);
		if (postcodePoolDto == null) {
			return new ResponseEntity<>(format("No PostcodePool found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(postcodePoolDto, HttpStatus.OK);
	}

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostcodePool(@PathVariable Long id) {
        if (!postcodePoolService.delete(id)) {
            return new ResponseEntity<>(format("No PostcodePool found for ID %d", id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("{id}/inner-numbers")
    @ResponseStatus(HttpStatus.OK)
    public List<BarcodeInnerNumberDto> getBarcodeInnerNumbers() {
        return barcodeInnerNumberService.getAll();
    }

}
