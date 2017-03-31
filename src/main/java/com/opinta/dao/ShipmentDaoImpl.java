package com.opinta.dao;

import com.opinta.entity.Client;
import com.opinta.entity.Shipment;
import com.opinta.entity.ShipmentGroup;
import com.opinta.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ShipmentDaoImpl implements ShipmentDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public ShipmentDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Shipment> getAll(User user) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Shipment.class, "shipment")
                .createCriteria("shipment.sender", "sender")
                .createCriteria("sender.counterparty", "counterparty")
                .add(Restrictions.eq("counterparty.user", user))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Shipment> getAllByClient(Client client, User user) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Shipment.class, "shipment")
                .add(Restrictions.eq("sender", client))
                .createCriteria("shipment.sender", "sender")
                .createCriteria("sender.counterparty", "counterparty")
                .add(Restrictions.eq("counterparty.user", user))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Shipment> getAllByShipmentGroup(ShipmentGroup shipmentGroup, User user) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Shipment.class, "shipment")
                .add(Restrictions.eq("shipmentGroup", shipmentGroup))
                .createCriteria("shipment.sender", "sender")
                .createCriteria("sender.counterparty", "counterparty")
                .add(Restrictions.eq("counterparty.user", user))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    public Shipment getByUuid(UUID uuid) {
        Session session = sessionFactory.getCurrentSession();
        return (Shipment) session.get(Shipment.class, uuid);
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
