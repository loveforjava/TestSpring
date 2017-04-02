package com.opinta.service;

import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;

import com.opinta.dto.PostcodePoolDto;
import com.opinta.entity.PostcodePool;
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
