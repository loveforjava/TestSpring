package com.opinta.mapper;

import com.opinta.dto.ClientDto;
import com.opinta.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Created by Diarsid on 20.03.2017.
 */

@Mapper(componentModel = "spring")
public interface ClientMapper
        extends BaseMapper<ClientDto, Client> {
    
    @Override
    @Mappings({
            @Mapping(source = "address.id", target = "addressId"),
            @Mapping(source = "virtualPostOffice.id", target = "virtualPostOfficeId")})
    ClientDto toDto(Client client);
    
    @Override
    @Mappings({
            @Mapping(source = "addressId", target = "address.id"),
            @Mapping(source = "virtualPostOfficeId", target = "virtualPostOffice.id")})
    Client toEntity(ClientDto clientDto);
}
