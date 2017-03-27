package com.opinta.mapper;

import com.opinta.dto.CounterpartyDto;
import com.opinta.entity.Counterparty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CounterpartyMapper
        extends BaseMapper<CounterpartyDto, Counterparty> {
    
    @Override
    @Mapping(source = "activePostcodePoolId", target = "activePostcodePool.id")
    Counterparty toEntity(CounterpartyDto dto);
    
    @Override
    @Mapping(source = "activePostcodePool.id", target = "activePostcodePoolId")
    CounterpartyDto toDto(Counterparty entity);
}
