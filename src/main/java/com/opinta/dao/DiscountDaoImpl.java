package com.opinta.dao;

import java.util.UUID;

import com.opinta.entity.Discount;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DiscountDaoImpl implements DiscountDao {
    private final SessionFactory sessionFactory;
    
    @Autowired
    public DiscountDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public Discount saveEntity(Discount entity) {
        Session session = this.sessionFactory.getCurrentSession();
        return (Discount) session.merge(entity);
    }
    
    @Override
    public Discount getEntityByUuid(UUID uuid) {
        Session session = this.sessionFactory.getCurrentSession();
        return (Discount) session.get(Discount.class, uuid);
    }
    
    @Override
    public Discount getEntityZeroValue() {
        Session session = this.sessionFactory.getCurrentSession();
        return (Discount) session.createCriteria(Discount.class)
                .add(Restrictions.eq("value", 0))
                .setMaxResults(1)
                .uniqueResult();
    }
    
    @Override
    public Discount getEntityByValue(float value) {
        Session session = this.sessionFactory.getCurrentSession();
        return (Discount) session.createCriteria(Discount.class)
                .add(Restrictions.eq("value", value))
                .setMaxResults(1)
                .uniqueResult();
    }
    
    @Override
    public void delete(Discount entity) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(entity);
    }
}
