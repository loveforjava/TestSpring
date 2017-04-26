package ua.ukrpost.dao;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;

import ua.ukrpost.entity.BarcodeInnerNumber;
import ua.ukrpost.entity.PostcodePool;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static ua.ukrpost.entity.BarcodeStatus.RESERVED;
import static java.lang.String.format;
import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;
import static java.time.LocalDateTime.now;

@Repository
public class BarcodeInnerNumberDaoImpl implements BarcodeInnerNumberDao {
    private final SessionFactory sessionFactory;
    
    @Autowired
    public BarcodeInnerNumberDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BarcodeInnerNumber> getAll(PostcodePool postcodePool) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(BarcodeInnerNumber.class, "barcodeInnerNumber")
                .add(Restrictions.eq("barcodeInnerNumber.postcodePool", postcodePool))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
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

    @Override
    public BarcodeInnerNumber generateForPostcodePool(PostcodePool postcodePool) {
        Session session = sessionFactory.getCurrentSession();
        String barcode = session.doReturningWork((connection) -> {
            try (CallableStatement call = connection.prepareCall(
                    "BEGIN " +
                    "   GET_NEXT_BARCODE(?, ?, ?); " +
                    "END;")) {
                call.setString(1, postcodePool.getUuid().toString().replaceAll("-", ""));
                call.registerOutParameter(2, VARCHAR);
                call.registerOutParameter(3, INTEGER);
                call.execute();
                return call.getString(2);
            } catch (SQLException e) {
                throw new RuntimeException(format("Can't generate barcode inner number from stored procedure: " +
                        "PostcodePool: %s", postcodePool.getUuid().toString().replaceAll("-", "")), e);
            }
        });
        BarcodeInnerNumber barcodeInnerNumber = new BarcodeInnerNumber();
        barcodeInnerNumber.setStatus(RESERVED);
        barcodeInnerNumber.setInnerNumber(barcode);
        barcodeInnerNumber.setPostcodePool(postcodePool);
        barcodeInnerNumber.setCreated(now());
        session.persist(barcodeInnerNumber);

        return barcodeInnerNumber;
    }
}
