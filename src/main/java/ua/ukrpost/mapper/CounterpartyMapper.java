package ua.ukrpost.mapper;

import ua.ukrpost.dto.CounterpartyDto;
import ua.ukrpost.entity.Counterparty;
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
