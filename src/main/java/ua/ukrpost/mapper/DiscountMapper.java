package ua.ukrpost.mapper;

import ua.ukrpost.dto.DiscountDto;
import ua.ukrpost.entity.Discount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper extends BaseMapper<DiscountDto, Discount> {
}
