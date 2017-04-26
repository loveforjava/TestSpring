package ua.ukrpost.dao;

import ua.ukrpost.entity.Counterparty;
import ua.ukrpost.entity.User;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import ua.ukrpost.entity.Client;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static java.sql.Types.VARCHAR;

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
    
    @Override
    public String getNextPostIdInnerNumber() {
        java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
        Session session = sessionFactory.getCurrentSession();
        return session.doReturningWork(connection -> {
            try (CallableStatement call = connection.prepareCall(
                    "BEGIN " +
                    "   GET_NEXT_CLIENT_POSTID(?, ?); " +
                    "END;")) {
                call.setDate(1, date);
                call.registerOutParameter(2, VARCHAR);
                call.execute();
                return call.getString(2);
            } catch (SQLException e) {
                throw new RuntimeException("Can't generate next postId inner number from stored procedure: ", e);
            }
        });
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
