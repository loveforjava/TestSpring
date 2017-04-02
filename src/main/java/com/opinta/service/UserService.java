package com.opinta.service;

import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import java.util.UUID;

public interface UserService {

    User getEntityByToken(UUID token);

    User authenticate(UUID token) throws AuthException;

    void authorizeForAction(Counterparty counterparty, User user) throws AuthException;

    void authorizeForAction(Client client, User user) throws AuthException;

    void authorizeForAction(Shipment shipment, User user) throws AuthException;

    void authorizeForAction(ShipmentGroup shipmentGroup, User user) throws AuthException;
}
