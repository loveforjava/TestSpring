package com.opinta.mapper;

import com.opinta.dto.DiscountPerCounterpartyDto;
import com.opinta.entity.DiscountPerCounterparty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DiscountPerCounterpartyMapper extends BaseMapper<DiscountPerCounterpartyDto, DiscountPerCounterparty> {

    @Override
    @Mappings({
            @Mapping(source = "counterparty.uuid", target = "counterpartyUuid"),
            @Mapping(source = "discount.uuid", target = "discountUuid")})
    DiscountPerCounterpartyDto toDto(DiscountPerCounterparty discountPerCounterparty);

    @Override
    @Mappings({
            @Mapping(source = "counterpartyUuid", target = "counterparty.uuid"),
            @Mapping(source = "discountUuid", target = "discount.uuid")})
    DiscountPerCounterparty toEntity(DiscountPerCounterpartyDto discountPerCounterpartyDto);
}
