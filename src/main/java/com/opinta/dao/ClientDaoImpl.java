package com.opinta.dao;

import java.util.List;

import com.opinta.entity.Client;
import com.opinta.entity.VirtualPostOffice;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClientDaoImpl implements ClientDao {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<Client> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Client.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Client> getAllByVirtualPostOffice(VirtualPostOffice virtualPostOffice) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Client.class)
                .add(Restrictions.eq("virtualPostOffice", virtualPostOffice))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Client getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Client) session.get(Client.class, id);
    }

    @Override
    public Client save(Client client) {
        Session session = sessionFactory.getCurrentSession();
        return (Client) session.merge(client);
    }

    @Override
    public void update(Client client) {
        Session session = sessionFactory.getCurrentSession();
        session.update(client);
    }

    @Override
    public void delete(Client client) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(client);
    }
}
