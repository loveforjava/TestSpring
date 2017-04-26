package ua.ukrpost.dao;

import ua.ukrpost.entity.classifier.City;

import java.util.List;

public interface CityDao {

    List<City> getAllCitiesByPostcode(String postcode);
}
