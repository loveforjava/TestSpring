package com.opinta.dao;

import com.opinta.model.Address;
import com.opinta.model.PostOffice;
import java.util.List;

public interface PostOfficeDao {
    List<PostOffice> getAll();
    PostOffice getById(Long id);
    PostOffice save(PostOffice postOffice);
    void update(PostOffice postOffice);
    void delete(PostOffice postOffice);
}
