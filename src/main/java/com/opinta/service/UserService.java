package com.opinta.service;

import com.opinta.dto.UserDto;
import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;

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
