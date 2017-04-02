package com.opinta.mapper;

import com.opinta.dto.PostOfficeDto;
import com.opinta.entity.PostOffice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PostOfficeMapper extends BaseMapper<PostOfficeDto, PostOffice> {

    @Override
    @Mappings({
            @Mapping(source = "address.id", target = "addressId"),
            @Mapping(source = "postcodePool.uuid", target = "postcodePoolUuid")
    })
    PostOfficeDto toDto(PostOffice postOffice);

    @Override
    @Mappings({
            @Mapping(source = "addressId", target = "address.id"),
            @Mapping(source = "postcodePoolUuid", target = "postcodePool.uuid")
    })
    PostOffice toEntity(PostOfficeDto postOfficeDto);
}
