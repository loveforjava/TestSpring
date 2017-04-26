package ua.ukrpost.mapper;

import ua.ukrpost.dto.AddressDto;
import ua.ukrpost.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper extends BaseMapper<AddressDto, Address> {
}
