package com.opinta.service;

import com.opinta.dto.DiscountPerCounterpartyDto;
import com.opinta.entity.DiscountPerCounterparty;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import com.opinta.exception.PerformProcessFailedException;
import java.util.List;
import java.util.UUID;

public interface DiscountPerCounterpartyService {

    List<DiscountPerCounterparty> getAllEntities(User user);

    DiscountPerCounterparty getEntityByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException;

    DiscountPerCounterparty saveEntity(DiscountPerCounterparty discountPerCounterparty, User user)
            throws AuthException, IncorrectInputDataException;

    List<DiscountPerCounterpartyDto> getAll(User user);

    DiscountPerCounterpartyDto getByUuid(UUID uuid, User user) throws IncorrectInputDataException,
            AuthException;

    DiscountPerCounterpartyDto save(DiscountPerCounterpartyDto discountPerCounterpartyDto, User user)
            throws IncorrectInputDataException, AuthException;

    DiscountPerCounterpartyDto update(UUID uuid, DiscountPerCounterpartyDto discountPerCounterpartyDto, User user)
            throws IncorrectInputDataException, AuthException, PerformProcessFailedException;

    void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
}
