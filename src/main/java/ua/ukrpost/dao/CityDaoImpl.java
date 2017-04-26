package ua.ukrpost.dao;

import ua.ukrpost.entity.classifier.City;
import ua.ukrpost.entity.classifier.CityPostcode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CityDaoImpl implements CityDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public CityDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<City> getAllCitiesByPostcode(String postcode) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(CityPostcode.class, "cityPostcode")
                .add(Restrictions.eq("cityPostcode.postcode", postcode))
                .setProjection(Projections.property("cityPostcode.city"))
                .list();
    }
}
