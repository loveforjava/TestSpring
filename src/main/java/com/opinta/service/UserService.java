package com.opinta.service;

import com.opinta.dto.UserDto;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.DiscountPerCounterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

public interface UserService {

    User getEntityByToken(UUID token);

    User getEntityById(long id);

    List<User> getUsersByCounterparty(Counterparty counterparty);

    UserDto save(UserDto userDto) throws IncorrectInputDataException;

    User saveEntity(User user) throws IncorrectInputDataException;

    void removeCounterpartyFromAllUsers(Counterparty counterparty) throws IncorrectInputDataException;

    void delete(long id) throws IncorrectInputDataException;

    User authenticate(UUID token) throws AuthException;

    void authorizeForAction(Counterparty counterparty, User user) throws AuthException;

    void authorizeForAction(Client client, User user) throws AuthException;

    void authorizeForAction(Shipment shipment, User user) throws AuthException;

    void authorizeForAction(ShipmentGroup shipmentGroup, User user) throws AuthException;

    void authorizeForAction(DiscountPerCounterparty discountPerCounterparty, User user) throws AuthException;
}
