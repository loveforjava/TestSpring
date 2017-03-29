package com.opinta.mapper;

import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface ClientMapper extends BaseMapper<ClientDto, Client> {
    
    @Override
    @Mappings({
            @Mapping(source = "address.id", target = "addressId"),
            @Mapping(source = "counterparty.id", target = "counterpartyId"),
            @Mapping(source = "phone.phoneNumber", target = "phoneNumber"),})
    ClientDto toDto(Client client);
    
    @Override
    @Mappings({
            @Mapping(source = "addressId", target = "address.id"),
            @Mapping(source = "counterpartyId", target = "counterparty.id"),
            @Mapping(source = "phoneNumber", target = "phone.phoneNumber")})
    Client toEntity(ClientDto clientDto);
}
