package com.opinta.service;

import com.opinta.model.Customer;
import com.opinta.model.PostcodePool;
import java.util.List;

public interface PostcodePoolService {
    List<PostcodePool> getAll();
    PostcodePool getById(Long id);
    void save(PostcodePool postcodePool);
    PostcodePool update(Long id, PostcodePool postcodePool);
    boolean delete(Long id);
}
