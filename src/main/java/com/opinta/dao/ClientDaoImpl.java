package com.opinta.dao;

import com.opinta.entity.Counterparty;
import java.util.List;
import java.util.UUID;

import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ClientDaoImpl implements ClientDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public ClientDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

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
    public List<Client> getAllByCounterparty(Counterparty counterparty) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Client.class)
                .add(Restrictions.eq("counterparty", counterparty))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Client getById(UUID id) {
        log.info("get Client by id: " + id);
        Session session = sessionFactory.getCurrentSession();
        return (Client) session.get(Client.class, id);
    }

    @Override
    public Client save(Client client) {
        Session session = sessionFactory.getCurrentSession();
        Client saved = (Client) session.merge(client);
        session.flush();
        return saved;
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
