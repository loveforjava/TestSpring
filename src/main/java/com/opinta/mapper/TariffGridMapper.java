package com.opinta.mapper;

import com.opinta.dto.classifier.TariffGridDto;
import com.opinta.entity.classifier.TariffGrid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TariffGridMapper extends BaseMapper<TariffGridDto, TariffGrid> {
}
