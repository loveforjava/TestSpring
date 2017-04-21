package com.opinta.util;

import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.entity.DiscountPerCounterparty;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import com.opinta.exception.AuthException;
import lombok.extern.slf4j.Slf4j;

import static com.opinta.util.LogMessageUtil.authorizationOnErrorLogEndpoint;

@Slf4j
public class AuthorizationUtil {

    public static void authorizeForAction(Counterparty counterparty, User user) throws AuthException {
        if (user == null || user.getCounterparty() == null
                || !user.getCounterparty().getUuid().equals(counterparty.getUuid())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), counterparty));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), counterparty));
        }
    }
    
    public static void authorizeForAction(Client client, User user) throws AuthException {
        if (user == null || client == null || user.getCounterparty() == null || client.getCounterparty() == null
                || client.getCounterparty() == null
                || !client.getCounterparty().getUuid().equals(user.getCounterparty().getUuid())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), client));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), client));
        }
    }
    
    public static void authorizeForAction(Shipment shipment, User user) throws AuthException {
        if (user == null || user.getCounterparty() == null || shipment == null
                || shipment.getSender().getCounterparty() == null || shipment.getSender().getCounterparty() == null
                || !shipment.getSender().getCounterparty().getUuid().equals(user.getCounterparty().getUuid())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), shipment));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), shipment));
        }
    }
    
    public static void authorizeForAction(ShipmentGroup shipmentGroup, User user) throws AuthException {
        if (user == null || user.getCounterparty() == null
                || shipmentGroup == null || shipmentGroup.getCounterparty() == null
                || shipmentGroup.getCounterparty() == null
                || !shipmentGroup.getCounterparty().getUuid().equals(user.getCounterparty().getUuid())) {
            assert user != null;
            log.error(authorizationOnErrorLogEndpoint(user.getToken(), shipmentGroup));
            throw new AuthException(authorizationOnErrorLogEndpoint(user.getToken(), shipmentGroup));
        }
    }
    
    public static void authorizeForAction(DiscountPerCounterparty discountPerCounterparty, User user)
            throws AuthException {
        authorizeForAction(discountPerCounterparty.getCounterparty(), user);
    }
}
