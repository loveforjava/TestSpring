package com.opinta.controller;

import com.opinta.dto.UserDto;
import com.opinta.entity.User;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.opinta.util.LogMessageUtil.saveOnErrorLogEndpoint;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDto userDto) {
        try {
            return new ResponseEntity<>(userService.save(userDto), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(saveOnErrorLogEndpoint(User.class, userDto, e), BAD_REQUEST);
        }
    }
}
