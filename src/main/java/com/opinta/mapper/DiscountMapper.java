package com.opinta.mapper;

import com.opinta.dto.DiscountDto;
import com.opinta.entity.Discount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper extends BaseMapper<DiscountDto, Discount> {
}
