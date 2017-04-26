package ua.ukrpost.mapper;

import ua.ukrpost.dto.PhoneDto;
import ua.ukrpost.entity.Phone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhoneMapper extends BaseMapper<PhoneDto, Phone> {
}
