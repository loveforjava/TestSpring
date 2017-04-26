package ua.ukrpost.service;

import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;
import java.util.List;

import ua.ukrpost.dto.PostcodePoolDto;
import ua.ukrpost.entity.PostcodePool;

import java.util.UUID;

public interface PostcodePoolService {

    List<PostcodePool> getAllEntities();

    PostcodePool getEntityByUuid(UUID uuid) throws IncorrectInputDataException;

    PostcodePool saveEntity(PostcodePool postcodePool);

    List<PostcodePoolDto> getAll();

    PostcodePoolDto getByUuid(UUID uuid) throws IncorrectInputDataException;
    
    PostcodePoolDto save(PostcodePoolDto postcodePoolDto);
    
    PostcodePoolDto update(UUID uuid, PostcodePoolDto postcodePoolDto) throws IncorrectInputDataException,
            PerformProcessFailedException;
    
    void delete(UUID uuid) throws IncorrectInputDataException;
}
