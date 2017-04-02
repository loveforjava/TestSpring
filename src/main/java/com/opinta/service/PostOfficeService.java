package com.opinta.service;

import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;

import com.opinta.dto.PostOfficeDto;
import com.opinta.entity.PostOffice;

public interface PostOfficeService {

    List<PostOffice> getAllEntities();

    PostOffice getEntityById(long id) throws IncorrectInputDataException;

    PostOffice saveEntity(PostOffice postOffice);
    
    List<PostOfficeDto> getAll();
    
    PostOfficeDto getById(long id) throws IncorrectInputDataException;
    
    PostOfficeDto save(PostOfficeDto postOfficeDto);
    
    PostOfficeDto update(long id, PostOfficeDto postOfficeDto) throws IncorrectInputDataException,
            PerformProcessFailedException;
    
    void delete(long id) throws IncorrectInputDataException;
}
