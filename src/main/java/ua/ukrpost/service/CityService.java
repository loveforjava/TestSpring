package ua.ukrpost.service;

import ua.ukrpost.dto.CityDto;
import java.util.List;

public interface CityService {

    List<CityDto> getAllCitiesByPostcode(String postcode);
}
