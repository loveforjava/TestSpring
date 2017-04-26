package ua.ukrpost.dao;

import ua.ukrpost.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import ua.ukrpost.entity.DiscountPerCounterparty;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DiscountPerCounterpartyDaoImpl implements DiscountPerCounterpartyDao {
    private final SessionFactory sessionFactory;
    
    @Autowired
    public DiscountPerCounterpartyDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DiscountPerCounterparty> getAll(User user) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(DiscountPerCounterparty.class, "discountPerCounterparty")
                .add(Restrictions.eq("discountPerCounterparty.counterparty", user.getCounterparty()))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public DiscountPerCounterparty getByUuid(UUID uuid) {
        Session session = sessionFactory.getCurrentSession();
        return (DiscountPerCounterparty) session.get(DiscountPerCounterparty.class, uuid);
    }

    @Override
    public DiscountPerCounterparty getHighestDiscount(User user, LocalDateTime date) {
        Session session = sessionFactory.getCurrentSession();
        return (DiscountPerCounterparty) session
                .createCriteria(DiscountPerCounterparty.class, "discountPerCounterparty")
                .createCriteria("discountPerCounterparty.discount", "discount")
                .add(Restrictions.eq("discountPerCounterparty.counterparty", user.getCounterparty()))
                .add(Restrictions.le("discountPerCounterparty.fromDate", date.toLocalDate()))
                .add(Restrictions.ge("discountPerCounterparty.toDate", date.toLocalDate()))
                .addOrder(Order.desc("discount.value"))
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public DiscountPerCounterparty save(DiscountPerCounterparty discountPerCounterparty) {
        Session session = sessionFactory.getCurrentSession();
        return (DiscountPerCounterparty) session.merge(discountPerCounterparty);
    }

    @Override
    public void update(DiscountPerCounterparty discountPerCounterparty) {
        Session session = sessionFactory.getCurrentSession();
        session.update(discountPerCounterparty);
    }

    @Override
    public void delete(DiscountPerCounterparty discountPerCounterparty) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(discountPerCounterparty);
    }
}
