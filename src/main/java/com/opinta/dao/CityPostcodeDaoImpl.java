package com.opinta.dao;

import com.opinta.entity.classifier.CityPostcode;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CityPostcodeDaoImpl implements CityPostcodeDao {
    @Override
    public List<CityPostcode> getAll() {
        return null;
    }

    @Override
    public CityPostcode getById(long id) {
        return null;
    }

    @Override
    public CityPostcode getByPostcode(String postcode) {
        return null;
    }
}
