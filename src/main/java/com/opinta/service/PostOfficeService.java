package com.opinta.service;

import java.util.List;

import com.opinta.dto.PostOfficeDto;

public interface PostOfficeService {
    
    List<PostOfficeDto> getAll();
    
    PostOfficeDto getById(long id);
    
    PostOfficeDto save(PostOfficeDto postOfficeDto);
    
    PostOfficeDto update(long id, PostOfficeDto postOfficeDto);
    
    boolean delete(long id);
}
