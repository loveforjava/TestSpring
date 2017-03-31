package com.opinta.dao;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.opinta.entity.BarcodeStatus.USED;

@Repository
public class BarcodeInnerNumberDaoImpl implements BarcodeInnerNumberDao {
    private final SessionFactory sessionFactory;
    
    @Autowired
    public BarcodeInnerNumberDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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
    
    private static final String BARCODE_INNER_CALL =
            "BEGIN GET_NEXT_BARCODE(?, ?, ?); " +
            "END;";
    @Override
    public BarcodeInnerNumber generateForPostcodePool(PostcodePool postcodePool) {
        Session session = sessionFactory.getCurrentSession();
        BarcodeInnerNumber barcodeInnerNumber = new BarcodeInnerNumber();
        barcodeInnerNumber.setStatus(USED);
        String barcode = session.doReturningWork((con) -> {
            try (CallableStatement call = con.prepareCall(BARCODE_INNER_CALL)) {
                call.setLong(1, postcodePool.getId());
                call.registerOutParameter(2, Types.VARCHAR);
                call.registerOutParameter(3, Types.INTEGER);
                call.execute();
                String obtainedBarcode = call.getString(2);
                return obtainedBarcode;
            } catch (SQLException e) {
                throw new RuntimeException("cannot get barcode inner number.", e);
            }
        });
        barcodeInnerNumber.setInnerNumber(barcode);
        session.persist(barcodeInnerNumber);
        return barcodeInnerNumber;
    }
    

//    private String getNextInnerNumber(long postcodeId) {
//        try (CallableStatement call = con.prepareCall(BARCODE_INNER_CALL)) {
//            call.setLong(1, postcodeId);
//            call.registerOutParameter(2, Types.VARCHAR);
//            call.registerOutParameter(3, Types.INTEGER);
//            call.execute();
//            String barcode = call.getString(2);
//            return barcode;
//        } catch (SQLException e) {
//            log.error("barcode inner number generation thrrough the stored procedure.", e);
//            throw new RuntimeException("cannot get barcode inner number.");
//        }
//    }
}
