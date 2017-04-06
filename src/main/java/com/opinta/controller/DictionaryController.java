package com.opinta.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

    @RequestMapping("/addresses")
    public ResponseEntity<?> getAddressesByPostcode(@RequestParam(value="postcode") String postcode) {
        return null;
    }

}
