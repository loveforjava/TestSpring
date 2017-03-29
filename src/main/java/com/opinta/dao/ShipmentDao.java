package com.opinta.dao;

import com.opinta.entity.Client;
import com.opinta.entity.Shipment;

import com.opinta.entity.User;
import java.util.List;
import javax.naming.AuthenticationException;

public interface ShipmentDao {

    List<Shipment> getAll(User user);

    List<Shipment> getAllByClient(Client client, User user);

    Shipment getById(long id);

    Shipment save(Shipment shipment);

    void update(Shipment shipment);

    void delete(Shipment shipment);
}
