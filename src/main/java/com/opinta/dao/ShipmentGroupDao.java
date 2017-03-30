package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;

import java.util.List;
import java.util.UUID;

public interface ShipmentGroupDao {

    List<ShipmentGroup> getAll(User user);

    List<ShipmentGroup> getAllByCounterparty(Counterparty counterparty);

    ShipmentGroup getById(UUID uuid);

    ShipmentGroup save(ShipmentGroup shipmentGroup);

    void update(ShipmentGroup shipmentGroup);

    void delete(ShipmentGroup shipmentGroup);
}
