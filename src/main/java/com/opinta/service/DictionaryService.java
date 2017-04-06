package com.opinta.service;

import com.opinta.entity.classifier.City;

import java.util.List;

public interface DictionaryService {

    List<City> getAllCitiesByPostcode(String postcode);

}
