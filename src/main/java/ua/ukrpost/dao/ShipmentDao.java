package ua.ukrpost.dao;

import ua.ukrpost.entity.Client;
import ua.ukrpost.entity.Shipment;

import ua.ukrpost.entity.ShipmentGroup;
import ua.ukrpost.entity.User;

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
