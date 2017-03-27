package com.opinta.service;

import java.util.List;

import com.opinta.dto.VirtualPostOfficeDto;
import com.opinta.entity.VirtualPostOffice;

public interface VirtualPostOfficeService {

    List<VirtualPostOffice> getAllEntities();

    VirtualPostOffice getEntityById(long id);

    VirtualPostOffice saveEntity(VirtualPostOffice virtualPostOffice);
    
    List<VirtualPostOfficeDto> getAll();
    
    VirtualPostOfficeDto getById(long id);
    
    VirtualPostOfficeDto update(long id, VirtualPostOfficeDto source);
    
    VirtualPostOfficeDto save(VirtualPostOfficeDto virtualPostOffice);
    
    boolean delete(long id);
}
