package com.opinta.dao;

import java.util.List;

import com.opinta.model.BarcodeInnerNumber;
import com.opinta.model.PostcodePool;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.opinta.model.BarcodeStatus.USED;

@Repository
public class BarcodeInnerNumberDaoImpl implements BarcodeInnerNumberDao {
    private final SessionFactory sessionFactory;
    private final BarcodeNextIndexGenerationStrategy barcodeGeneration;
    
    @Autowired
    public BarcodeInnerNumberDaoImpl(
            SessionFactory sessionFactory, BarcodeNextIndexGenerationStrategy barcodeNextIndexGenerationStrategy) {
        this.sessionFactory = sessionFactory;
        this.barcodeGeneration = barcodeNextIndexGenerationStrategy;
    }

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
    public BarcodeInnerNumber generateForPostcodePool(PostcodePool postcodePool) {
        BarcodeInnerNumber barcodeInnerNumber = new BarcodeInnerNumber();
        barcodeInnerNumber.setStatus(USED);
        barcodeInnerNumber.setNumber(barcodeGeneration.newInnerNumberFor(postcodePool.getPostcode()));
        //barcodeInnerNumber.setPostcode(postcodePool);
        sessionFactory.getCurrentSession().save(barcodeInnerNumber);
        return barcodeInnerNumber;
    }


//    @Override
//    public BarcodeInnerNumber save(BarcodeInnerNumber barcodeInnerNumber) {
//        Session session = sessionFactory.getCurrentSession();
//        return (BarcodeInnerNumber) session.merge(barcodeInnerNumber);
//    }

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
