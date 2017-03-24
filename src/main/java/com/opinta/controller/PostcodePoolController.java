package com.opinta.controller;

import java.util.List;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.dto.PostcodePoolDto;
import com.opinta.service.BarcodeInnerNumberService;
import com.opinta.service.PostcodePoolService;
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
	public ResponseEntity<?> getPostcodePool(@PathVariable("id") long id) {
		PostcodePoolDto postcodePoolDto = postcodePoolService.getById(id);
		if (postcodePoolDto == null) {
			return new ResponseEntity<>(format("No PostcodePool found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(postcodePoolDto, HttpStatus.OK);
	}

	@PostMapping
    @ResponseStatus(HttpStatus.OK)
	public PostcodePoolDto createPostcodePool(@RequestBody PostcodePoolDto postcodePoolDto) {
		return postcodePoolService.save(postcodePoolDto);
	}

	@PutMapping("{id}")
	public ResponseEntity<?> updatePostcodePool(@PathVariable long id, @RequestBody PostcodePoolDto postcodePoolDto) {
        postcodePoolDto = postcodePoolService.update(id, postcodePoolDto);
		if (postcodePoolDto == null) {
			return new ResponseEntity<>(format("No PostcodePool found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(postcodePoolDto, HttpStatus.OK);
	}

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostcodePool(@PathVariable long id) {
        if (!postcodePoolService.delete(id)) {
            return new ResponseEntity<>(format("No PostcodePool found for ID %d", id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("{postcodeId}/inner-numbers")
    public ResponseEntity<?> getBarcodeInnerNumbers(@PathVariable long postcodeId) {
		List<BarcodeInnerNumberDto> barcodeInnerNumberDtos = barcodeInnerNumberService.getAll(postcodeId);
		if (barcodeInnerNumberDtos == null) {
			return new ResponseEntity<>(format("PostcodePool %d doesn't exist", postcodeId), HttpStatus.NOT_FOUND);
		}
        return new ResponseEntity<>(barcodeInnerNumberDtos, HttpStatus.OK);
    }

	@GetMapping("inner-numbers/{id}")
	public ResponseEntity<?> getBarcodeInnerNumber(@PathVariable("id") long id) {
		BarcodeInnerNumberDto barcodeInnerNumberDto = barcodeInnerNumberService.getById(id);
		if (barcodeInnerNumberDto == null) {
			return new ResponseEntity<>(format("No barcodeInnerNumber found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(barcodeInnerNumberDto, HttpStatus.OK);
	}

	@PostMapping("{postcodeId}/inner-numbers")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> createBarcodeInnerNumber(
			@PathVariable("postcodeId") long postcodeId, @RequestBody BarcodeInnerNumberDto barcodeInnerNumberDto) {
		BarcodeInnerNumberDto barcodeInnerNumberDtoSaved = barcodeInnerNumberService.save(postcodeId, barcodeInnerNumberDto);
		if (barcodeInnerNumberDtoSaved == null) {
			return new ResponseEntity<>(format("PostcodePool %d doesn't exist", postcodeId), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(barcodeInnerNumberDtoSaved, HttpStatus.OK);
	}

	@DeleteMapping("inner-numbers/{id}")
	public ResponseEntity<?> deleteBarcodeInnerNumber(@PathVariable long id) {
		if (!barcodeInnerNumberService.delete(id)) {
			return new ResponseEntity<>(format("No barcodeInnerNumberService found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(id, HttpStatus.OK);
	}
}
