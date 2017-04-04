package com.opinta.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan({"com.opinta"})
@PropertySource(value = {
        "classpath:application.properties",
        "classpath:application.dev.properties"})
@Slf4j
public class HibernateConfig {
    private Environment environment;
    @Value("classpath:sql/db-data-countryside-postcode.sql")
    private Resource dataScript_countryside_postcode;
    @Value("classpath:sql/db-data-tariff-grid.sql")
    private Resource dataScript_tariff_grid;

    @Autowired
    public HibernateConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "sessionFactory")
    @Profile("prod")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.opinta.entity");
        sessionFactory.setHibernateProperties(hibernatePropertiesProduction());
        return sessionFactory;
    }

    @Bean(name = "sessionFactory")
    @Profile("dev")
    public LocalSessionFactoryBean sessionFactoryDevelopment() {
        log.info("ACTIVE SPRING PROFILE: dev");
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.opinta.entity");
        sessionFactory.setHibernateProperties(hibernatePropertiesDevelopment());
        return sessionFactory;
    }

    @Bean(name = "dataSource")
    @Profile("prod")
    public DataSource dataSource() {
        log.info("ACTIVE SPRING PROFILE: prod");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("prod.jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("prod.jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("prod.jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("prod.jdbc.password"));
        return dataSource;
    }

    @Bean(name = "dataSource")
    @Profile("dev")
    public DataSource dataSourceDevelopment() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("dev.jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("dev.jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("dev.jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("dev.jdbc.password"));
        return dataSource;
    }

    private Properties hibernatePropertiesProduction() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("prod.hibernate.dialect"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("prod.hibernate.format_sql"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("prod.hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("prod.hibernate.hbm2ddl.auto"));
        return properties;
    }
    
    private Properties hibernatePropertiesDevelopment() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("dev.hibernate.dialect"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("dev.hibernate.format_sql"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("dev.hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("dev.hibernate.hbm2ddl.auto"));
        return properties;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory s) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(s);
        return txManager;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(dataScript_countryside_postcode);
        populator.addScript(dataScript_tariff_grid);
        return populator;
    }
}
