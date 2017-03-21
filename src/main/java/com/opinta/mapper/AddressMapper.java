package com.opinta.mapper;

import com.opinta.dto.AddressDto;
import com.opinta.model.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper
        extends BaseMapper<AddressDto, Address> {
}
