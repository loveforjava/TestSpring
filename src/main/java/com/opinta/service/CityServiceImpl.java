package com.opinta.service;

import com.opinta.dao.CityDao;
import com.opinta.dto.CityDto;
import com.opinta.mapper.CityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
public class CityServiceImpl implements CityService {
    private CityDao cityDao;
    private CityMapper cityMapper;

    @Autowired
    public CityServiceImpl(CityDao cityDao, CityMapper cityMapper) {
        this.cityDao = cityDao;
        this.cityMapper = cityMapper;
    }

    @Override
    @Transactional
    public List<CityDto> getAllCitiesByPostcode(String postcode) {
        return cityMapper.toDto(cityDao.getAllCitiesByPostcode(postcode));
    }
}
