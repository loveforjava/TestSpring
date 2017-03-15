package com.opinta.dao;

import com.opinta.model.Customer;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerDaoImpl implements CustomerDao {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<Customer> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Customer.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Customer getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Customer) session.get(Customer.class, id);
    }

    @Override
    public void save(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(customer);
    }

    @Override
    public void update(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.update(customer);
    }

    @Override
    public void delete(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(customer);
    }

}
