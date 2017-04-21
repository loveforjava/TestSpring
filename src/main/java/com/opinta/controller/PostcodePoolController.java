package com.opinta.controller;

import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;

import com.opinta.dto.PostcodePoolDto;
import com.opinta.service.BarcodeInnerNumberService;
import com.opinta.service.PostcodePoolService;
import java.util.UUID;
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

import static com.opinta.util.LogMessageUtil.getAllOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/postcodes")
public class PostcodePoolController extends BaseController {
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

    @GetMapping("{uuid}")
    public ResponseEntity<?> getPostcodePool(@PathVariable UUID uuid) {
        try {
            return new ResponseEntity<>(postcodePoolService.getByUuid(uuid), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(PostcodePool.class, uuid, e), NOT_FOUND);
        }
    }

    @PostMapping
    @ResponseStatus(OK)
    public PostcodePoolDto createPostcodePool(@RequestBody @Valid PostcodePoolDto postcodePoolDto) {
        return postcodePoolService.save(postcodePoolDto);
    }

    @PutMapping("{uuid}")
    public ResponseEntity<?> updatePostcodePool(@PathVariable UUID uuid,
                                                @RequestBody @Valid PostcodePoolDto postcodePoolDto) {
        try {
            return new ResponseEntity<>(postcodePoolService.update(uuid, postcodePoolDto), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(PostcodePool.class, uuid, e), NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(PostcodePool.class, uuid, e), BAD_REQUEST);
        }
    }

    @GetMapping("{postcodePoolUuid}/inner-numbers")
    public ResponseEntity<?> getBarcodeInnerNumbers(@PathVariable UUID postcodePoolUuid) {
        try {
            return new ResponseEntity<>(barcodeInnerNumberService.getAll(postcodePoolUuid), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getAllOnErrorLogEndpoint(BarcodeInnerNumber.class, e), NOT_FOUND);
        }
    }

    @GetMapping("inner-numbers/{id}")
    public ResponseEntity<?> getBarcodeInnerNumber(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(barcodeInnerNumberService.getById(id), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(BarcodeInnerNumber.class, id, e), NOT_FOUND);
        }
    }
}
