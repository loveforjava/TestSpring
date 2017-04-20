package com.opinta.dao;

import com.opinta.entity.Counterparty;
import com.opinta.entity.User;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.opinta.entity.Client;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static java.lang.String.format;

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
                .add(Restrictions.eq("client.counterparty", user.getCounterparty()))
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
    public Client getByUuid(UUID uuid) {
        Session session = sessionFactory.getCurrentSession();
        return (Client) session.get(Client.class, uuid);
    }
    
    @Override
    public Client getByPostId(String postId) {
        Session session = sessionFactory.getCurrentSession();
        return (Client) session.createCriteria(Client.class)
                .add(Restrictions.eq("postId", postId))
                .setMaxResults(1)
                .uniqueResult();
    }
    
    private static final String POST_ID_NEXT_NUMBER_CALL =
            "BEGIN " +
            "   GET_CLIENT_POSTID(?, ?); " +
            "END;";
    @Override
    public String getNextPostIdNumber() {
        java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
        Session session = sessionFactory.getCurrentSession();
        int nextNumber = session.doReturningWork(connection -> {
            try (CallableStatement call = connection.prepareCall(POST_ID_NEXT_NUMBER_CALL)) {
                call.setDate(1, date);
                call.registerOutParameter(2, Types.INTEGER);
                call.execute();
                return call.getInt(2);
            } catch (SQLException e) {
                throw new RuntimeException("Can't generate next postid number from stored procedure: ", e);
            }
        });
        return format("%07d", nextNumber);
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
