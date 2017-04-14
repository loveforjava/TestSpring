package com.opinta.controller;

import java.util.UUID;

import com.opinta.dto.DiscountPerCounterpartyDto;
import com.opinta.entity.DiscountPerCounterparty;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import com.opinta.service.DiscountPerCounterpartyService;
import com.opinta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.opinta.util.LogMessageUtil.authorizationOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.deleteOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getAllOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.saveOnErrorLogEndpoint;
import static com.opinta.util.LogMessageUtil.updateOnErrorLogEndpoint;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/counterparty-discounts")
public class DiscountPerCounterpartyController extends BaseController {
    private final DiscountPerCounterpartyService discountPerCounterpartyService;
    private final UserService userService;
    
    @Autowired
    public DiscountPerCounterpartyController(DiscountPerCounterpartyService discountPerCounterpartyService,
                                             UserService userService) {
        this.discountPerCounterpartyService = discountPerCounterpartyService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllDiscountsPerCounterparty(@RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(discountPerCounterpartyService.getAll(user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(getAllOnErrorLogEndpoint(DiscountPerCounterparty.class, e), UNAUTHORIZED);
        }
    }

    @GetMapping("{uuid}")
    public ResponseEntity<?> getDiscountPerCounterpartyByUuid(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(discountPerCounterpartyService.getByUuid(uuid, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(authorizationOnErrorLogEndpoint(token, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(DiscountPerCounterparty.class, e), NOT_FOUND);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createDiscountPerCounterparty(
            @RequestBody DiscountPerCounterpartyDto discountPerCounterpartyDto, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(discountPerCounterpartyService.save(discountPerCounterpartyDto, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(authorizationOnErrorLogEndpoint(token, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(saveOnErrorLogEndpoint(DiscountPerCounterparty.class,
                    discountPerCounterpartyDto, e), NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(saveOnErrorLogEndpoint(DiscountPerCounterparty.class,
                    discountPerCounterpartyDto, e), BAD_REQUEST);
        }
    }
    
    @PutMapping("{uuid}")
    public ResponseEntity<?> updateDiscountPerCounterparty(@PathVariable UUID uuid,
            @RequestBody DiscountPerCounterpartyDto discountPerCounterpartyDto, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            return new ResponseEntity<>(discountPerCounterpartyService
                    .update(uuid, discountPerCounterpartyDto, user), OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(authorizationOnErrorLogEndpoint(token, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(DiscountPerCounterparty.class,
                    discountPerCounterpartyDto, e), NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(DiscountPerCounterparty.class,
                    discountPerCounterpartyDto, e), BAD_REQUEST);
        }
    }
    
    @DeleteMapping("{uuid}")
    public ResponseEntity<?> deleteDiscount(@PathVariable UUID uuid, @RequestParam UUID token) {
        try {
            User user = userService.authenticate(token);
            discountPerCounterpartyService.delete(uuid, user);
            return new ResponseEntity<>(OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(authorizationOnErrorLogEndpoint(token, e), UNAUTHORIZED);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(deleteOnErrorLogEndpoint(DiscountPerCounterparty.class, uuid, e), NOT_FOUND);
        }
    }
}
