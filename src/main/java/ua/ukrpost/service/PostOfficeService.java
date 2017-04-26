package ua.ukrpost.service;

import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import java.util.List;

import ua.ukrpost.dto.PostOfficeDto;
import ua.ukrpost.entity.PostOffice;

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
