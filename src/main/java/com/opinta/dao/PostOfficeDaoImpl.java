package com.opinta.dao;

import com.opinta.entity.PostOffice;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PostOfficeDaoImpl implements PostOfficeDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public PostOfficeDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PostOffice> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(PostOffice.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public PostOffice getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (PostOffice) session.get(PostOffice.class, id);
    }

    @Override
    public PostOffice save(PostOffice postOffice) {
        Session session = sessionFactory.getCurrentSession();
        return (PostOffice) session.merge(postOffice);
    }

    @Override
    public void update(PostOffice postOffice) {
        Session session = sessionFactory.getCurrentSession();
        session.update(postOffice);
    }

    @Override
    public void delete(PostOffice postOffice) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(postOffice);
    }
}
