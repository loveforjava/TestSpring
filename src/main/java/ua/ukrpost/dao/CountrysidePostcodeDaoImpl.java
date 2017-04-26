package ua.ukrpost.dao;

import ua.ukrpost.entity.classifier.CountrysidePostcode;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CountrysidePostcodeDaoImpl implements CountrysidePostcodeDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public CountrysidePostcodeDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CountrysidePostcode> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(CountrysidePostcode.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public CountrysidePostcode getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (CountrysidePostcode) session.get(CountrysidePostcode.class, id);
    }

    @Override
    public CountrysidePostcode getByPostcode(String postcode) {
        Session session = sessionFactory.getCurrentSession();
        return (CountrysidePostcode) session.createCriteria(CountrysidePostcode.class)
                .add(Restrictions.eq("postcode", postcode))
                .setMaxResults(1)
                .uniqueResult();
    }
}
