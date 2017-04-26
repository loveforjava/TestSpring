package ua.ukrpost.dao;

import ua.ukrpost.entity.ShipmentTrackingDetail;

import java.util.List;

public interface ShipmentTrackingDetailDao {

    List<ShipmentTrackingDetail> getAll();

    ShipmentTrackingDetail getById(long id);

    ShipmentTrackingDetail save(ShipmentTrackingDetail shipmentTrackingDetail);

    void update(ShipmentTrackingDetail shipmentTrackingDetail);

    void delete(ShipmentTrackingDetail shipmentTrackingDetail);
}
