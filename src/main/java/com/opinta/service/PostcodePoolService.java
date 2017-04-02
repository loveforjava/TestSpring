package com.opinta.service;

import java.util.List;

import com.opinta.dto.PostcodePoolDto;
import com.opinta.entity.PostcodePool;
import java.util.UUID;

public interface PostcodePoolService {

    List<PostcodePool> getAllEntities();

    PostcodePool getEntityByUuid(UUID uuid);

    PostcodePool saveEntity(PostcodePool postcodePool);

    List<PostcodePoolDto> getAll();

    PostcodePoolDto getByUuid(UUID uuid);
    
    PostcodePoolDto save(PostcodePoolDto postcodePoolDto);
    
    PostcodePoolDto update(UUID uuid, PostcodePoolDto postcodePoolDto);
    
    boolean delete(UUID uuid);
}
