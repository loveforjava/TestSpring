package com.opinta.service;

import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;

public interface BarcodeInnerNumberGenerator {
    BarcodeInnerNumber generate(PostcodePool postcodePool);
}
