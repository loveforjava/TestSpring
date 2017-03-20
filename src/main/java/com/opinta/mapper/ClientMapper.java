package com.opinta.mapper;

import com.opinta.dto.ClientDto;
import com.opinta.model.Client;
import org.mapstruct.Mapper;

/**
 * Created by Diarsid on 20.03.2017.
 */

@Mapper(componentModel = "spring")
public interface ClientMapper
        extends BaseMapper<ClientDto, Client> {
}
