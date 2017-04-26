package ua.ukrpost.mapper;

import ua.ukrpost.dto.BarcodeInnerNumberDto;
import ua.ukrpost.entity.BarcodeInnerNumber;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BarcodeInnerNumberMapper extends BaseMapper<BarcodeInnerNumberDto, BarcodeInnerNumber> {
}
