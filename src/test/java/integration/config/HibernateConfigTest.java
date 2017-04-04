package integration.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class HibernateConfigTest {
    private Environment environment;

    @Autowired
    public HibernateConfigTest(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "sessionFactory")
    @Profile("dev")
    public LocalSessionFactoryBean sessionFactoryTest() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSourceTest());
        sessionFactory.setPackagesToScan("com.opinta.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean(name = "sessionFactory")
    @Profile("memory")
    public LocalSessionFactoryBean sessionFactoryTestInMemory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSourceTestInMemory());
        sessionFactory.setPackagesToScan("com.opinta.entity");
        sessionFactory.setHibernateProperties(hibernatePropertiesInMemory());
        return sessionFactory;
    }

    @Bean(name = "dataSource")
    @Profile("dev")
    public DataSource dataSourceTest() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("test.oracle.jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("test.oracle.jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("test.oracle.jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("test.oracle.jdbc.password"));
        return dataSource;
    }

    @Bean(name = "dataSource")
    @Profile("memory")
    public DataSource dataSourceTestInMemory() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("test.memory.jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("test.memory.jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("test.memory.jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("test.memory.jdbc.password"));
        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("test.oracle.hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("test.oracle.hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("test.oracle.hibernate.format_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("test.oracle.hibernate.hbm2ddl.auto"));
        return properties;
    }

    private Properties hibernatePropertiesInMemory() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("test.memory.hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("test.memory.hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("test.memory.hibernate.format_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("test.memory.hibernate.hbm2ddl.auto"));
        return properties;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory s) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(s);
        return txManager;
    }
}
