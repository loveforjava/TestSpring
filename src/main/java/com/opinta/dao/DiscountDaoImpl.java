package com.opinta.dao;

import java.util.List;
import java.util.UUID;

import com.opinta.entity.Discount;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
    public List<Discount> getAllEntities() {
        Session session = this.sessionFactory.getCurrentSession();
        return session.createCriteria(Discount.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }
    
    @Override
    public void delete(Discount entity) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(entity);
    }
}
