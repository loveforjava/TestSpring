package com.opinta.service;

import com.opinta.dao.BarcodeInnerNumberDao;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.BarcodeStatus;
import com.opinta.entity.PostcodePool;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("dev")
public class BarcodeInnerNumberGeneratorImplForInMemoryDd implements BarcodeInnerNumberGenerator {
    private BarcodeInnerNumberDao barcodeInnerNumberDao;

    @Autowired
    public BarcodeInnerNumberGeneratorImplForInMemoryDd(BarcodeInnerNumberDao barcodeInnerNumberDao) {
        this.barcodeInnerNumberDao = barcodeInnerNumberDao;
    }

    @Override
    public BarcodeInnerNumber generate(PostcodePool postcodePool) {
        Random random = new Random();
        int min = 11111111;
        int max = 99999999;
        Integer randomNum = random.nextInt((max - min) + 1) + min;
        BarcodeInnerNumber barcodeInnerNumber = new BarcodeInnerNumber(randomNum.toString(), BarcodeStatus.RESERVED);
        barcodeInnerNumber = barcodeInnerNumberDao.save(barcodeInnerNumber);
        log.info("generated barcode: {}", randomNum);
        return barcodeInnerNumber;
    }
}
