package com.opinta.service;

import java.util.List;

import com.opinta.dto.PostcodePoolDto;
import com.opinta.entity.PostcodePool;

public interface PostcodePoolService {

    List<PostcodePool> getAllEntities();

    PostcodePool getEntityById(long id);

    PostcodePool saveEntity(PostcodePool postcodePool);

    List<PostcodePoolDto> getAll();

    PostcodePoolDto getById(long id);
    
    PostcodePoolDto save(PostcodePoolDto postcodePoolDto);
    
    PostcodePoolDto update(long id, PostcodePoolDto postcodePoolDto);
    
    boolean delete(long id);
}
