package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.PostcodePool;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class CounterpartyDaoImpl implements CounterpartyDao {
    private final SessionFactory sessionFactory;
    
    @Autowired
    public CounterpartyDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Counterparty> getAll() {
        Session session = this.sessionFactory.getCurrentSession();
        return session.createCriteria(Counterparty.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Counterparty getById(long id) {
        Session session = this.sessionFactory.getCurrentSession();
        return (Counterparty) session.get(Counterparty.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Counterparty> getByPostcodePool(PostcodePool postcodePool) {
        Session session = this.sessionFactory.getCurrentSession();
        return session.createCriteria(Counterparty.class)
                .add(Restrictions.eq("postcodePool", postcodePool))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Counterparty save(Counterparty counterparty) {
        Session session = this.sessionFactory.getCurrentSession();
        return (Counterparty) session.merge(counterparty);
    }

    @Override
    public void update(Counterparty counterparty) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(counterparty);
    }

    @Override
    public void delete(Counterparty counterparty) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(counterparty);
    }
}
