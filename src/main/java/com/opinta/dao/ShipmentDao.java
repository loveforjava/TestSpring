package com.opinta.dao;

import com.opinta.entity.Client;
import com.opinta.entity.Shipment;

import java.util.List;

public interface ShipmentDao {

    List<Shipment> getAll();

    List<Shipment> getAllByClient(Client client);

    Shipment getById(long id);

    Shipment save(Shipment shipment);

    void update(Shipment shipment);

    void delete(Shipment shipment);
}
