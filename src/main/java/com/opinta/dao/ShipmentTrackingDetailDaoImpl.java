package com.opinta.dao;

import com.opinta.entity.ShipmentTrackingDetail;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ShipmentTrackingDetailDaoImpl implements ShipmentTrackingDetailDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public ShipmentTrackingDetailDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ShipmentTrackingDetail> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(ShipmentTrackingDetail.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public ShipmentTrackingDetail getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (ShipmentTrackingDetail) session.get(ShipmentTrackingDetail.class, id);
    }

    @Override
    public ShipmentTrackingDetail save(ShipmentTrackingDetail shipmentTrackingDetail) {
        Session session = sessionFactory.getCurrentSession();
        return (ShipmentTrackingDetail) session.merge(shipmentTrackingDetail);
    }

    @Override
    public void update(ShipmentTrackingDetail shipmentTrackingDetail) {
        Session session = sessionFactory.getCurrentSession();
        session.update(shipmentTrackingDetail);
    }

    @Override
    public void delete(ShipmentTrackingDetail shipmentTrackingDetail) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(shipmentTrackingDetail);
    }
}
