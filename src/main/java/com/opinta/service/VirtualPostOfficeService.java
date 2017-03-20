package com.opinta.service;

import java.util.List;

import com.opinta.dto.VirtualPostOfficeDto;

public interface VirtualPostOfficeService {
    
    List<VirtualPostOfficeDto> getAll();
    
    VirtualPostOfficeDto getById(long id);
    
    VirtualPostOfficeDto update(long id, VirtualPostOfficeDto source);
    
    boolean save(VirtualPostOfficeDto virtualPostOffice);
    
    boolean delete(long id);
}
