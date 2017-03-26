package com.opinta.dao;

import java.util.List;

import com.opinta.entity.PostcodePool;

public interface PostcodePoolDao {

    List<PostcodePool> getAll();

    PostcodePool getById(long id);

    PostcodePool save(PostcodePool postcodePool);

    void update(PostcodePool postcodePool);

    void delete(PostcodePool postcodePool);
}
