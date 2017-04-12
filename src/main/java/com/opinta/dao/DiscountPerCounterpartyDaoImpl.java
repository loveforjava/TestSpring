package com.opinta.dao;

import java.util.UUID;

import com.opinta.entity.Counterparty;
import com.opinta.entity.DiscountPerCounterparty;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DiscountPerCounterpartyDaoImpl implements DiscountPerCounterpartyDao {
    private final SessionFactory sessionFactory;
    
    @Autowired
    public DiscountPerCounterpartyDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public DiscountPerCounterparty saveEntity(DiscountPerCounterparty entity) {
        Session session = this.sessionFactory.getCurrentSession();
        return (DiscountPerCounterparty) session.merge(entity);
    }
    
    @Override
    public DiscountPerCounterparty getEntityByUuid(UUID uuid) {
        Session session = this.sessionFactory.getCurrentSession();
        return (DiscountPerCounterparty) session.get(DiscountPerCounterpartyDao.class, uuid);
    }
    
    @Override
    public DiscountPerCounterparty getEntityByCounterparty(Counterparty counterparty) {
        Session session = this.sessionFactory.getCurrentSession();
        return (DiscountPerCounterparty) session.createCriteria(DiscountPerCounterparty.class)
                .add(Restrictions.eq("counterparty", counterparty))
                .setMaxResults(1)
                .uniqueResult();
    }
    
    @Override
    public void delete(DiscountPerCounterparty entity) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(entity);
    }
}
