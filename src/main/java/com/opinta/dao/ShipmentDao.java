package com.opinta.dao;

import com.opinta.entity.Client;
import com.opinta.entity.Shipment;

import java.util.List;
import java.util.UUID;

public interface ShipmentDao {

    List<Shipment> getAll();

    List<Shipment> getAllByClient(Client client);

    Shipment getByUuid(UUID uuid);

    Shipment save(Shipment shipment);

    void update(Shipment shipment);

    void delete(Shipment shipment);
}
