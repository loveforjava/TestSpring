package com.opinta.mapper;

import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.model.BarcodeInnerNumber;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BarcodeInnerNumberMapper
        extends BaseMapper<BarcodeInnerNumberDto, BarcodeInnerNumber> {
}
