package com.opinta.service;

import com.opinta.entity.Shipment;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class ShipmentCalculation {
    private TariffGridService tariffGridService;

    @Autowired
    public ShipmentCalculation(TariffGridService tariffGridService) {
        this.tariffGridService = tariffGridService;
    }

    public BigDecimal calculatePrice(Shipment shipment) {
        log.info("Calculating price for shipment {}", shipment);
        // TODO define w2wVariation from the data of the shipment (where and to)
        // if the town is the same - w2wVariation = TOWN
        // if the region is the same - w2wVariation = REGION
        // otherwise  - w2wVariation = COUNTRY
        W2wVariation w2wVariation = W2wVariation.REGION;
        TariffGrid tariffGrid = tariffGridService
                .getPriceByDimension(shipment.getWeight(), shipment.getLength(), w2wVariation);
        log.info("TariffGrid for weight {} and length {}: {}", shipment.getWeight(), shipment.getLength(), tariffGrid);
        if (tariffGrid == null) {
            return new BigDecimal("0");
        }
        return new BigDecimal(Float.toString(tariffGrid.getPrice()));
    }
}
