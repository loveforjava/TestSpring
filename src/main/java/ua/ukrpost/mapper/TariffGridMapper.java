package ua.ukrpost.mapper;

import ua.ukrpost.dto.classifier.TariffGridDto;
import ua.ukrpost.entity.classifier.TariffGrid;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TariffGridMapper extends BaseMapper<TariffGridDto, TariffGrid> {
}
