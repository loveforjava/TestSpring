package ua.ukrpost.service;

import ua.ukrpost.entity.BarcodeInnerNumber;
import ua.ukrpost.entity.PostcodePool;

public interface BarcodeInnerNumberGenerator {
    BarcodeInnerNumber generate(PostcodePool postcodePool);
}
