package com.opinta.dao;

import com.opinta.entity.classifier.City;
import com.opinta.entity.classifier.CityPostcode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public class CityPostcodeDaoImpl implements CityPostcodeDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public CityPostcodeDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public List<City> getAllCitiesByPostcode(String postcode) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(CityPostcode.class, "cityPostcode")
                .add(Restrictions.eq("cityPostcode.postcode", postcode))
                .setProjection(Projections.property("cityPostcode.city"))
                .list();
    }
}
