package ua.ukrpost.dao;

import ua.ukrpost.entity.classifier.CountrysidePostcode;

import java.util.List;

public interface CountrysidePostcodeDao {

    List<CountrysidePostcode> getAll();

    CountrysidePostcode getById(long id);

    CountrysidePostcode getByPostcode(String postcode);
}
