package com.opinta.dao;

import com.opinta.entity.Phone;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhoneDaoImpl implements PhoneDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public PhoneDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Phone> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Phone.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Phone getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Phone) session.get(Phone.class, id);
    }

    @Override
    public Phone getByPhoneNumber(String phoneNumber) {
        Session session = sessionFactory.getCurrentSession();
        return (Phone) session.createCriteria(Phone.class)
                .add(Restrictions.eq("phoneNumber", phoneNumber))
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public Phone save(Phone phone) {
        Session session = sessionFactory.getCurrentSession();
        return (Phone) session.merge(phone);
    }

    @Override
    public void update(Phone phone) {
        Session session = sessionFactory.getCurrentSession();
        session.update(phone);
    }

    @Override
    public void delete(Phone phone) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(phone);
    }
}
