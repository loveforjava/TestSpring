package com.opinta.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(value = "dummy")
public class BarcodeNextIndexDummyGeneration implements BarcodeNextIndexGenerationStrategy {
    
    private static final Map<String, Integer> POSTCODE_COUNTERS = new HashMap<>();
        
    private final Object barcodeGenerationLock;
    
    public BarcodeNextIndexDummyGeneration() {
        this.barcodeGenerationLock = new Object();
    }
    
    @Override
    public String newInnerNumberFor(String postcode) {
        synchronized (barcodeGenerationLock) {
            POSTCODE_COUNTERS.putIfAbsent(postcode, 1);
            int postcodeCounter = POSTCODE_COUNTERS.get(postcode);
            POSTCODE_COUNTERS.put(postcode, postcodeCounter + 1);
            return this.indexToProperFormat(postcodeCounter);
        }
    }
}
