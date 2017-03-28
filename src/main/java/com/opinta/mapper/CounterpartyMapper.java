package com.opinta.mapper;

import com.opinta.dto.CounterpartyDto;
import com.opinta.entity.Counterparty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface CounterpartyMapper extends BaseMapper<CounterpartyDto, Counterparty> {
    
    @Override
    @Mappings({
            @Mapping(source = "postcodePoolId", target = "postcodePool.id"),
            @Mapping(source = "id", target = "uuid")})
    Counterparty toEntity(CounterpartyDto dto);
    
    @Override
    @Mappings({
            @Mapping(source = "postcodePool.id", target = "postcodePoolId"),
            @Mapping(source = "uuid", target = "id")})
    CounterpartyDto toDto(Counterparty entity);
}
