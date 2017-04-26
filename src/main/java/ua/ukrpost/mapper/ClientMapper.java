package ua.ukrpost.mapper;

import ua.ukrpost.dto.ClientDto;
import ua.ukrpost.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface ClientMapper extends BaseMapper<ClientDto, Client> {
    
    @Override
    @Mappings({
            @Mapping(source = "address.id", target = "addressId"),
            @Mapping(source = "counterparty.uuid", target = "counterpartyUuid"),
            @Mapping(source = "phone.phoneNumber", target = "phoneNumber")})
    ClientDto toDto(Client client);
    
    @Override
    @Mappings({
            @Mapping(source = "addressId", target = "address.id"),
            @Mapping(source = "counterpartyUuid", target = "counterparty.uuid"),
            @Mapping(source = "phoneNumber", target = "phone.phoneNumber")})
    Client toEntity(ClientDto clientDto);
}
