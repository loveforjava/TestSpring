package com.opinta.controller;

import com.opinta.entity.PostOffice;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;

import com.opinta.dto.PostOfficeDto;
import com.opinta.service.PostOfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/post-offices")
public class PostOfficeController extends BaseController {
    private PostOfficeService postOfficeService;

    @Autowired
    public PostOfficeController(PostOfficeService postOfficeService) {
        this.postOfficeService = postOfficeService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<PostOfficeDto> getPostOffices() {
        return postOfficeService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getPostOffice(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(postOfficeService.getById(id), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(PostOffice.class, id, e), NOT_FOUND);
        }
    }

    @PostMapping
    @ResponseStatus(OK)
    public ResponseEntity<?> createPostOffice(@RequestBody @Valid PostOfficeDto postOfficeDto) {
        return new ResponseEntity<>(postOfficeService.save(postOfficeDto), OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePostOffice(@PathVariable @Valid long id, @RequestBody PostOfficeDto postOfficeDto) {
        try {
            return new ResponseEntity<>(postOfficeService.update(id, postOfficeDto), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(PostOffice.class, id, e), NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(PostOffice.class, id, e), BAD_REQUEST);
        }
    }
}
