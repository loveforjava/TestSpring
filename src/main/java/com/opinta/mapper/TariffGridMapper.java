package com.opinta.mapper;

import java.util.List;

import com.opinta.dto.classifier.TariffGridDto;
import com.opinta.entity.classifier.TariffGrid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TariffGridMapper extends BaseMapper<TariffGridDto, TariffGrid> {
    
    @Override
    List<TariffGrid> toEntity(List<TariffGridDto> dtos);
    
    @Override
    TariffGrid toEntity(TariffGridDto dto);
    
    @Override
    List<TariffGridDto> toDto(List<TariffGrid> entity);
    
    @Override
    TariffGridDto toDto(TariffGrid entity);
}
