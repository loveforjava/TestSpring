package com.opinta.dao;

import com.opinta.entity.classifier.CityPostcode;

import java.util.List;

public interface CityPostcodeDao {

    List<CityPostcode> getAll();

    CityPostcode getById(long id);

    CityPostcode getByPostcode(String postcode);

}
