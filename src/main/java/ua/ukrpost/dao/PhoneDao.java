package ua.ukrpost.dao;

import ua.ukrpost.entity.Phone;

import java.util.List;

public interface PhoneDao {

    List<Phone> getAll();

    Phone getById(long id);

    Phone getByPhoneNumber(String phoneNumber);

    Phone save(Phone phone);

    void update(Phone phone);

    void delete(Phone phone);
}
