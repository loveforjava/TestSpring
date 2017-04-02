package com.opinta.dao;

import java.util.List;

import com.opinta.entity.PostcodePool;
import java.util.UUID;

public interface PostcodePoolDao {

    List<PostcodePool> getAll();

    PostcodePool getByUuid(UUID uuid);

    PostcodePool save(PostcodePool postcodePool);

    void update(PostcodePool postcodePool);

    void delete(PostcodePool postcodePool);
}
