package ua.ukrpost.dao;

import java.util.List;

import ua.ukrpost.entity.PostcodePool;

import java.util.UUID;

public interface PostcodePoolDao {

    List<PostcodePool> getAll();

    PostcodePool getByUuid(UUID uuid);

    PostcodePool save(PostcodePool postcodePool);

    void update(PostcodePool postcodePool);

    void delete(PostcodePool postcodePool);
}
