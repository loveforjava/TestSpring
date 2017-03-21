package com.opinta.dao;

import java.util.List;

import com.opinta.model.BarcodeInnerNumber;
import com.opinta.model.PostcodePool;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BarcodeInnerNumberDaoImpl implements BarcodeInnerNumberDao {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<BarcodeInnerNumber> getAll(long postcodeId) {
        // TODO think about getting directly table BarcodeInnerNumber
        Session session = sessionFactory.getCurrentSession();
        PostcodePool postcodePool = (PostcodePool) session.get(PostcodePool.class, postcodeId);
        if (postcodePool == null) {
            return null;
        }
        return postcodePool.getBarcodeInnerNumbers();
    }

    @Override
    public BarcodeInnerNumber getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (BarcodeInnerNumber) session.get(BarcodeInnerNumber.class, id);
    }

    @Override
    public BarcodeInnerNumber save(BarcodeInnerNumber barcodeInnerNumber) {
        Session session = sessionFactory.getCurrentSession();
        return (BarcodeInnerNumber) session.merge(barcodeInnerNumber);
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
