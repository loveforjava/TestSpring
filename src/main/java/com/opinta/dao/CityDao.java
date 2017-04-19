package com.opinta.dao;

import com.opinta.entity.classifier.City;

import java.util.List;

public interface CityDao {

    List<City> getAllCitiesByPostcode(String postcode);
}
