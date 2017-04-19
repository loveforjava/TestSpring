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
            @Mapping(source = "postcodePool.uuid", target = "postcodePoolUuid")})
    CounterpartyDto toDto(Counterparty entity);

    @Override
    @Mappings({
            @Mapping(source = "postcodePoolUuid", target = "postcodePool.uuid")})
    Counterparty toEntity(CounterpartyDto dto);
}
