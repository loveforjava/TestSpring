package com.opinta.service;

import javax.transaction.Transactional;

import com.opinta.dao.CountrysidePostcodeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountrysidePostcodeServiceImpl implements CountrysidePostcodeService {
    private final CountrysidePostcodeDao countrysidePostcodeDao;
    
    @Autowired
    public CountrysidePostcodeServiceImpl(CountrysidePostcodeDao countrysidePostcodeDao) {
        this.countrysidePostcodeDao = countrysidePostcodeDao;
    }
    
    @Override
    @Transactional
    public boolean isPostcodeInCountryside(String postcode) {
        return (countrysidePostcodeDao.getByPostcode(postcode) != null);
    }
}
