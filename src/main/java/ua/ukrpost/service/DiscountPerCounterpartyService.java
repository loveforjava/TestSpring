package ua.ukrpost.service;

import ua.ukrpost.dto.DiscountPerCounterpartyDto;
import ua.ukrpost.entity.DiscountPerCounterparty;
import ua.ukrpost.entity.User;
import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import ua.ukrpost.exception.PerformProcessFailedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DiscountPerCounterpartyService {

    List<DiscountPerCounterparty> getAllEntities(User user);

    DiscountPerCounterparty getEntityByUuid(UUID uuid, User user) throws IncorrectInputDataException, AuthException;

    DiscountPerCounterparty getEntityWithHighestDiscount(User user, LocalDateTime date);

    DiscountPerCounterparty saveEntity(DiscountPerCounterparty discountPerCounterparty, User user)
            throws AuthException, IncorrectInputDataException, PerformProcessFailedException;

    DiscountPerCounterparty updateEntity(UUID uuid, DiscountPerCounterparty source, User user)
            throws IncorrectInputDataException, PerformProcessFailedException, AuthException;

    List<DiscountPerCounterpartyDto> getAll(User user);

    DiscountPerCounterpartyDto getByUuid(UUID uuid, User user) throws IncorrectInputDataException,
            AuthException;

    DiscountPerCounterpartyDto save(DiscountPerCounterpartyDto discountPerCounterpartyDto, User user)
            throws IncorrectInputDataException, AuthException, PerformProcessFailedException;

    DiscountPerCounterpartyDto update(UUID uuid, DiscountPerCounterpartyDto discountPerCounterpartyDto, User user)
            throws IncorrectInputDataException, AuthException, PerformProcessFailedException;

    void delete(UUID uuid, User user) throws AuthException, IncorrectInputDataException;
}
