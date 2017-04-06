package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.User;
import java.util.List;
import java.util.UUID;

import com.opinta.entity.Client;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClientDaoImpl implements ClientDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public ClientDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Client> getAll(User user) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Client.class, "client")
                .createCriteria("client.counterparty", "counterparty")
                .add(Restrictions.eq("counterparty.user", user))
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
    public List<Client> getAllSendersByCounterparty(Counterparty counterparty) {
        return getAllClientsByCounterpartyAndType(counterparty, true);
    }
    
    @Override
    public List<Client> getAllRecipientsByCounterparty(Counterparty counterparty) {
        return getAllClientsByCounterpartyAndType(counterparty, false);
    }
    
    private List<Client> getAllClientsByCounterpartyAndType(Counterparty counterparty, boolean isSender) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Client.class)
                .add(Restrictions.eq("counterparty", counterparty))
                .add(Restrictions.eq("sender", isSender))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }
    
    @Override
    public Client getByUuid(UUID uuid) {
        Session session = sessionFactory.getCurrentSession();
        return (Client) session.get(Client.class, uuid);
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
