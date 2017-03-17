package com.opinta.dao;

import com.opinta.model.VirtualPostOffice;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class VirtualPostOfficeDaoImpl implements VirtualPostOfficeDao {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<VirtualPostOffice> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(VirtualPostOffice.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public VirtualPostOffice getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return (VirtualPostOffice) session.get(VirtualPostOffice.class, id);
    }

    @Override
    public void save(VirtualPostOffice virtualPostOffice) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(virtualPostOffice);
    }

    @Override
    public void update(VirtualPostOffice virtualPostOffice) {
        Session session = sessionFactory.getCurrentSession();
        session.update(virtualPostOffice);
    }

    @Override
    public void delete(VirtualPostOffice virtualPostOffice) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(virtualPostOffice);
    }
}
