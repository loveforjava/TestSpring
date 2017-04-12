package com.opinta.mapper;

import com.opinta.dto.CityDto;
import com.opinta.entity.classifier.City;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CityMapper extends BaseMapper<CityDto, City> {
    @Override
    @Mappings({
            @Mapping(source = "country.name", target = "country"),
            @Mapping(source = "region.name", target = "region"),
            @Mapping(source = "district.name", target = "district")})
    CityDto toDto(City city);

    @Override
    @Mappings({
            @Mapping(source = "country", target = "country.name"),
            @Mapping(source = "region", target = "region.name"),
            @Mapping(source = "district", target = "district.name")})
    City toEntity(CityDto cityDto);
}
