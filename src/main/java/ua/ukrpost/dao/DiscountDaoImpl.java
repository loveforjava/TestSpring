package ua.ukrpost.dao;

import java.util.List;
import java.util.UUID;

import ua.ukrpost.entity.Discount;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DiscountDaoImpl implements DiscountDao {
    private final SessionFactory sessionFactory;
    
    @Autowired
    public DiscountDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Discount> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Discount.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Discount getByUuid(UUID uuid) {
        Session session = sessionFactory.getCurrentSession();
        return (Discount) session.get(Discount.class, uuid);
    }

    @Override
    public Discount save(Discount entity) {
        Session session = sessionFactory.getCurrentSession();
        return (Discount) session.merge(entity);
    }

    @Override
    public void update(Discount discount) {
        Session session = sessionFactory.getCurrentSession();
        session.update(discount);
    }

    @Override
    public void delete(Discount entity) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(entity);
    }
}
