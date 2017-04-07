package com.opinta.dao;

import com.opinta.entity.classifier.City;
import com.opinta.entity.classifier.CityPostcode;

import java.util.List;

public interface CityDao {

    List<City> getAllCitiesByPostcode(String postcode);

}
