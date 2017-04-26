package ua.ukrpost.service;

import ua.ukrpost.dto.UserDto;
import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User getEntityByToken(UUID token);

    User getEntityById(long id);

    List<User> getUsersByCounterparty(Counterparty counterparty);

    User saveEntity(User user) throws IncorrectInputDataException, AuthException;

    User updateEntity(User user) throws IncorrectInputDataException, AuthException;

    UserDto save(UserDto userDto) throws IncorrectInputDataException, AuthException;

    void delete(long id) throws IncorrectInputDataException;

    User authenticate(UUID token) throws AuthException;
}
