package com.opinta.service;

import com.opinta.dao.BarcodeInnerNumberDao;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"stage", "dev"})
public class BarcodeInnerNumberGeneratorImpl implements BarcodeInnerNumberGenerator {
    private BarcodeInnerNumberDao barcodeInnerNumberDao;

    @Autowired
    public BarcodeInnerNumberGeneratorImpl(BarcodeInnerNumberDao barcodeInnerNumberDao) {
        this.barcodeInnerNumberDao = barcodeInnerNumberDao;
    }

    @Override
    public BarcodeInnerNumber generate(PostcodePool postcodePool) {
        BarcodeInnerNumber barcode = barcodeInnerNumberDao.generateForPostcodePool(postcodePool);
        log.info("generated barcodeInnerNumber: " + barcode.toString());
        return barcode;
    }
}
