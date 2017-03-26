package com.opinta.dao;

import java.util.List;

import com.opinta.entity.VirtualPostOffice;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class VirtualPostOfficeDaoImpl implements VirtualPostOfficeDao {
    
    private final SessionFactory sessionFactory;
    
    @Autowired
    public VirtualPostOfficeDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<VirtualPostOffice> getAll() {
        Session session = this.sessionFactory.getCurrentSession();
        return session.createCriteria(VirtualPostOffice.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public VirtualPostOffice getById(long id) {
        Session session = this.sessionFactory.getCurrentSession();
        return (VirtualPostOffice) session.get(VirtualPostOffice.class, id);
    }

    @Override
    public VirtualPostOffice save(VirtualPostOffice virtualPostOffice) {
        Session session = this.sessionFactory.getCurrentSession();
        log.info("saving new virtual post office: " + virtualPostOffice);
        virtualPostOffice = (VirtualPostOffice) session.merge(virtualPostOffice);
        log.info("virtual post office saved with id: " + virtualPostOffice.getId());
        return virtualPostOffice;
    }

    @Override
    public boolean update(VirtualPostOffice virtualPostOffice) {
        log.info("updating virtual post office using id: " + virtualPostOffice.getId());
        Session session = this.sessionFactory.getCurrentSession();
        session.update(virtualPostOffice);
        log.info("virtual post office updated with id: " + virtualPostOffice.getId());
        return true;
    }

    @Override
    public boolean delete(VirtualPostOffice virtualPostOffice) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(virtualPostOffice);
        return true;
    }
}
