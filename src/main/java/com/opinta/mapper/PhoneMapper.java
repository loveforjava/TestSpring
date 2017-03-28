package com.opinta.mapper;

import com.opinta.dto.PhoneDto;
import com.opinta.entity.Phone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhoneMapper extends BaseMapper<PhoneDto, Phone> {
}
