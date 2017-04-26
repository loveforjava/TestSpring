package ua.ukrpost.dao;

import ua.ukrpost.entity.PostOffice;

import java.util.List;

public interface PostOfficeDao {

    List<PostOffice> getAll();

    PostOffice getById(long id);

    PostOffice save(PostOffice postOffice);

    void update(PostOffice postOffice);

    void delete(PostOffice postOffice);
}
