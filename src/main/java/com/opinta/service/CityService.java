package com.opinta.service;

import com.opinta.dto.CityDto;
import java.util.List;

public interface CityService {

    List<CityDto> getAllCitiesByPostcode(String postcode);
}
