package com.opinta.controller;

import java.util.List;

import com.opinta.dto.PostOfficeDto;
import com.opinta.service.PostOfficeService;
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
@RequestMapping("/post-offices")
public class PostOfficeController {
    private PostOfficeService postOfficeService;

    @Autowired
    public PostOfficeController(PostOfficeService postOfficeService) {
        this.postOfficeService = postOfficeService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostOfficeDto> getPostOffices() {
        return postOfficeService.getAll();
    }

	@GetMapping("{id}")
	public ResponseEntity<?> getPostOffice(@PathVariable("id") long id) {
        PostOfficeDto postOfficeDto = postOfficeService.getById(id);
		if (postOfficeDto == null) {
			return new ResponseEntity<>(format("No PostOffice found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(postOfficeDto, HttpStatus.OK);
	}

	@PostMapping
    @ResponseStatus(HttpStatus.OK)
	public void createPostOffice(@RequestBody PostOfficeDto postOfficeDto) {
		postOfficeService.save(postOfficeDto);
	}

	@PutMapping("{id}")
	public ResponseEntity<?> updatePostOffice(@PathVariable long id, @RequestBody PostOfficeDto postOfficeDto) {
		postOfficeDto = postOfficeService.update(id, postOfficeDto);
		if (postOfficeDto== null) {
			return new ResponseEntity<>(format("No PostOfficeDto found for ID %d", id), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(postOfficeDto, HttpStatus.OK);
	}

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostOffice(@PathVariable long id) {
        if (!postOfficeService.delete(id)) {
            return new ResponseEntity<>(format("No PostOfficeDto found for ID %d", id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
