package com.opinta.dao;

import java.util.List;

import com.opinta.entity.Address;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AddressDaoImpl implements AddressDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public AddressDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Address> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Address.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Address getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Address) session.get(Address.class, id);
    }

    @Override
    public Address save(Address address) {
        Session session = sessionFactory.getCurrentSession();
        return (Address) session.merge(address);
    }

    @Override
    public void update(Address address) {
        Session session = sessionFactory.getCurrentSession();
        session.update(address);
    }

    @Override
    public void delete(Address address) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(address);
    }
}
