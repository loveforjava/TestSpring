package com.opinta.service;

import com.opinta.entity.Shipment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class ShipmentCalculation {
    public BigDecimal calculatePrice(Shipment shipment) {
        log.info("Calculating price for shipment {}", shipment);
        return null;
    }
}
