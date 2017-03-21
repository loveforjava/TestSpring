package com.opinta.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

/**
 * Created by Diarsid on 20.03.2017.
 */
public class ResponseEntityUtils {
    
    private ResponseEntityUtils() {
    }
    
    public static ResponseEntity ok() {
        return new ResponseEntity(HttpStatus.OK);
    }
    
    public static <T>  ResponseEntity<T> okWith(T entity) {
        return new ResponseEntity(entity, OK);
    }
    
    public static ResponseEntity badRequest(String message) {
        return new ResponseEntity(message, BAD_REQUEST);
    }
    
    public static ResponseEntity notFound(String message) {
        return new ResponseEntity(message, NOT_FOUND);
    }
}
