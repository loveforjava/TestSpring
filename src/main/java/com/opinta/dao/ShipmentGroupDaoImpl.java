package com.opinta.dao;

import com.opinta.entity.Counterparty;
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
public class ShipmentGroupDaoImpl implements ShipmentGroupDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public ShipmentGroupDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ShipmentGroup> getAll(User user) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(ShipmentGroup.class, "shipmentGroup")
                .createCriteria("shipmentGroup.counterparty", "counterparty")
                .add(Restrictions.eq("counterparty.user", user))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ShipmentGroup> getAllByCounterparty(Counterparty counterparty) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(ShipmentGroup.class)
                .add(Restrictions.eq("counterparty", counterparty))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public ShipmentGroup getById(UUID uuid) {
        Session session = sessionFactory.getCurrentSession();
        return (ShipmentGroup) session.get(ShipmentGroup.class, uuid);
    }

    @Override
    public ShipmentGroup save(ShipmentGroup shipmentGroup) {
        Session session = sessionFactory.getCurrentSession();
        return (ShipmentGroup) session.merge(shipmentGroup);
    }

    @Override
    public void update(ShipmentGroup shipmentGroup) {
        Session session = sessionFactory.getCurrentSession();
        session.update(shipmentGroup);
    }

    @Override
    public void delete(ShipmentGroup shipmentGroup) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(shipmentGroup);
    }
}
