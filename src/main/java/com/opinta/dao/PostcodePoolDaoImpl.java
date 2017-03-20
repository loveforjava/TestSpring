package com.opinta.dao;

import com.opinta.model.PostcodePool;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PostcodePoolDaoImpl implements PostcodePoolDao {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<PostcodePool> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(PostcodePool.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public PostcodePool getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return (PostcodePool) session.get(PostcodePool.class, id);
    }

    @Override
    public PostcodePool save(PostcodePool postcodePool) {
        Session session = sessionFactory.getCurrentSession();
        return (PostcodePool) session.merge(postcodePool);
    }

    @Override
    public void update(PostcodePool postcodePool) {
        Session session = sessionFactory.getCurrentSession();
        session.update(postcodePool);
    }

    @Override
    public void delete(PostcodePool postcodePool) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(postcodePool);
    }
}
