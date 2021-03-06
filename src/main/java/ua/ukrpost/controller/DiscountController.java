package ua.ukrpost.controller;

import ua.ukrpost.entity.Discount;
import ua.ukrpost.exception.PerformProcessFailedException;
import java.util.UUID;

import ua.ukrpost.dto.DiscountDto;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static ua.ukrpost.util.LogMessageUtil.getByIdOnErrorLogEndpoint;
import static ua.ukrpost.util.LogMessageUtil.updateOnErrorLogEndpoint;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/discounts")
public class DiscountController extends BaseController {
    private final DiscountService discountService;
    
    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllDiscounts() {
        return new ResponseEntity<>(discountService.getAll(), OK);
    }
    
    @GetMapping("{uuid}")
    public ResponseEntity<?> getDiscountByUuid(@PathVariable UUID uuid) {
        try {
            return new ResponseEntity<>(discountService.getByUuid(uuid), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(getByIdOnErrorLogEndpoint(Discount.class, uuid, e), NOT_FOUND);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createDiscount(@RequestBody @Valid DiscountDto discountDto) {
        return new ResponseEntity<>(discountService.save(discountDto), OK);
    }

    @PutMapping("{uuid}")
    public ResponseEntity<?> updateDiscount(@PathVariable @Valid UUID uuid, @RequestBody DiscountDto discountDto) {
        try {
            return new ResponseEntity<>(discountService.update(uuid, discountDto), OK);
        } catch (IncorrectInputDataException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Discount.class, discountDto, e), NOT_FOUND);
        } catch (PerformProcessFailedException e) {
            return new ResponseEntity<>(updateOnErrorLogEndpoint(Discount.class, discountDto, e), BAD_REQUEST);
        }
    }
}
