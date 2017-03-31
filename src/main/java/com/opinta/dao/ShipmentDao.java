package com.opinta.dao;

import com.opinta.entity.Client;
import com.opinta.entity.Shipment;

import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import java.util.List;
import java.util.UUID;

public interface ShipmentDao {

    List<Shipment> getAll(User user);

    List<Shipment> getAllByClient(Client client, User user);

    List<Shipment> getAllByShipmentGroup(ShipmentGroup shipmentGroup, User user);

    Shipment getByUuid(UUID uuid);

    Shipment save(Shipment shipment);

    void update(Shipment shipment);

    void delete(Shipment shipment);
}
