package com.opinta.mapper;

import com.opinta.dto.VirtualPostOfficeDto;
import com.opinta.entity.VirtualPostOffice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface VirtualPostOfficeMapper
        extends BaseMapper<VirtualPostOfficeDto, VirtualPostOffice> {
    
    @Override
    @Mapping(source = "activePostcodePoolId", target = "activePostcodePool.id")
    VirtualPostOffice toEntity(VirtualPostOfficeDto dto);
    
    @Override
    @Mapping(source = "activePostcodePool.id", target = "activePostcodePoolId")
    VirtualPostOfficeDto toDto(VirtualPostOffice entity);
}
