package com.opinta.mapper;

import com.opinta.dto.JuridicalClientDto;
import com.opinta.entity.JuridicalClient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface JuridicalClientMapper extends BaseMapper<JuridicalClientDto, JuridicalClient> {
    
    @Override
    @Mappings({
            @Mapping(source = "address.id", target = "addressId"),
            @Mapping(source = "counterparty.uuid", target = "counterpartyUuid"),
            @Mapping(source = "phone.phoneNumber", target = "phoneNumber")})
    JuridicalClientDto toDto(JuridicalClient entity);
    
    @Override
    @Mappings({
            @Mapping(source = "addressId", target = "address.id"),
            @Mapping(source = "counterpartyUuid", target = "counterparty.uuid"),
            @Mapping(source = "phoneNumber", target = "phone.phoneNumber")})
    JuridicalClient toEntity(JuridicalClientDto dto);
}
