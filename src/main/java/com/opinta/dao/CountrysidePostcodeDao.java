package com.opinta.dao;

import com.opinta.entity.CountrysidePostcode;
import java.util.List;

public interface CountrysidePostcodeDao {

    List<CountrysidePostcode> getAll();

    CountrysidePostcode getById(long id);

    CountrysidePostcode getByPostcode(String postcode);
}
