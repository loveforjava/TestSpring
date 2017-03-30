package com.opinta.service;

import com.opinta.dto.CounterpartyDto;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.User;
import java.util.UUID;
import javax.naming.AuthenticationException;

public interface UserService {

    User getEntityByToken(UUID token);

    User authenticate(UUID token) throws AuthenticationException;

    void authorizeForAction(Counterparty counterparty, User user) throws AuthenticationException;

    void authorizeForAction(Client client, User user) throws AuthenticationException;

    void authorizeForAction(Shipment shipment, User user) throws AuthenticationException;
}
