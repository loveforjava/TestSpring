package com.opinta.dao;

import com.opinta.model.Address;
import com.opinta.model.TariffGrid;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TariffGridDaoImpl implements TariffGridDao {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<TariffGrid> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(TariffGrid.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public TariffGrid getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (TariffGrid) session.get(TariffGrid.class, id);
    }

    @Override
    public TariffGrid save(TariffGrid tariffGrid) {
        Session session = sessionFactory.getCurrentSession();
        return (TariffGrid) session.merge(tariffGrid);
    }

    @Override
    public void update(TariffGrid tariffGrid) {
        Session session = sessionFactory.getCurrentSession();
        session.update(tariffGrid);
    }

    @Override
    public void delete(TariffGrid tariffGrid) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(tariffGrid);
    }

    @Override
    public TariffGrid getPriceByDimension(float weight, float length) {
        String id = "id";
        Session session = sessionFactory.getCurrentSession();
        DetachedCriteria maxId = DetachedCriteria.forClass(TariffGrid.class).setProjection(Projections.max(id));
        session.createCriteria(TariffGrid.class)
//                .add() // where weight <= weight and length <= length
                .add(Restrictions.le("weight", weight))
                .add(Restrictions.le("length", length))
                .add(Property.forName(id).eq(maxId))
                .list();

        return null;
    }
}
