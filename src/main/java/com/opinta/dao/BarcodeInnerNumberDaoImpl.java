package com.opinta.dao;

import com.opinta.model.BarcodeInnerNumber;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BarcodeInnerNumberDaoImpl implements BarcodeInnerNumberDao {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<BarcodeInnerNumber> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(BarcodeInnerNumber.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public BarcodeInnerNumber getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return (BarcodeInnerNumber) session.get(BarcodeInnerNumber.class, id);
    }

    @Override
    public void save(BarcodeInnerNumber barcodeInnerNumber) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(barcodeInnerNumber);
    }

    @Override
    public void update(BarcodeInnerNumber barcodeInnerNumber) {
        Session session = sessionFactory.getCurrentSession();
        session.update(barcodeInnerNumber);
    }

    @Override
    public void delete(BarcodeInnerNumber barcodeInnerNumber) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(barcodeInnerNumber);
    }
}
