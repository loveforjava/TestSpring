package com.opinta.mapper;

import com.opinta.dto.VirtualPostOfficeDto;
import com.opinta.model.VirtualPostOffice;
import org.mapstruct.Mapper;

/**
 * Created by Diarsid on 20.03.2017.
 */

@Mapper(componentModel = "spring")
public interface VirtualPostOfficeMapper
        extends BaseMapper<VirtualPostOfficeDto, VirtualPostOffice> {
}
