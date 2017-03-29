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
            @Mapping(source = "uuid", target = "id"),
            @Mapping(source = "address.id", target = "addressId"),
            @Mapping(source = "counterparty.uuid", target = "counterpartyId"),
            @Mapping(source = "phone.phoneNumber", target = "phoneNumber")})
    ClientDto toDto(Client client);
    
    @Override
    @Mappings({
            @Mapping(source = "id", target = "uuid"),
            @Mapping(source = "addressId", target = "address.id"),
            @Mapping(source = "counterpartyId", target = "counterparty.uuid"),
            @Mapping(source = "phoneNumber", target = "phone.phoneNumber")})
    Client toEntity(ClientDto clientDto);
}
