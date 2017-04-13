package com.opinta.mapper;

import com.opinta.dto.DiscountPerCounterpartyDto;
import com.opinta.entity.DiscountPerCounterparty;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountPerCounterpartyMapper extends BaseMapper<DiscountPerCounterpartyDto, DiscountPerCounterparty> {
}
