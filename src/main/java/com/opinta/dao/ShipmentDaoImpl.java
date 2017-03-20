package com.opinta.dao;

import com.opinta.model.Shipment;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShipmentDaoImpl implements ShipmentDao {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<Shipment> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Shipment.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Shipment getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Shipment) session.get(Shipment.class, id);
    }

    @Override
    public Shipment save(Shipment shipment) {
        Session session = sessionFactory.getCurrentSession();
        return (Shipment) session.merge(shipment);
    }

    @Override
    public void update(Shipment shipment) {
        Session session = sessionFactory.getCurrentSession();
        session.update(shipment);
    }

    @Override
    public void delete(Shipment shipment) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(shipment);
    }
}
