package com.opinta.dao;

import com.opinta.model.Customer;
import com.opinta.model.PostcodePool;
import java.util.List;

public interface PostcodePoolDao {
    List<PostcodePool> getAll();
    PostcodePool getById(Long id);
    void save(PostcodePool postcodePool);
    void update(PostcodePool postcodePool);
    void delete(PostcodePool postcodePool);
}
