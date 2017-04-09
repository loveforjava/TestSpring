package com.opinta.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
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
@ComponentScan("com.opinta")
@PropertySource(value = {
        "classpath:application.properties",
        "classpath:application-dev.properties"})
@Slf4j
public class HibernateConfig {
    private static final String PACKAGE_TO_SCAN = "com.opinta.entity";
    private Environment environment;

    @Value("classpath:db/migration/prod/V3.1__populate_country.sql.sql")
    private Resource countryPopulatorProd;
    @Value("classpath:db/migration/prod/V3.2__populate_region.sql.sql")
    private Resource regionPopulatorProd;
    @Value("classpath:db/migration/prod/V3.3__populate_district.sql")
    private Resource districtPopulatorProd;
    @Value("classpath:db/migration/prod/V3.4__populate_city.sql")
    private Resource cityPopulatorProd;
    @Value("classpath:db/migration/prod/V3.5__populate_countryside_postcode.sql")
    private Resource countrysidePostcodePopulatorProd;
    @Value("classpath:db/migration/prod/V3.6__populate_tariff_grid.sql")
    private Resource tariffGridPopulatorProd;
    @Value("classpath:db/migration/prod/V3.7_populate_city_postcode.sql")
    private Resource postcodePopulatorProd;

    @Value("classpath:db/migration/dev/V3.1__populate_country.sql")
    private Resource countryPopulatorMemory;
    @Value("classpath:db/migration/dev/V3.2__populate_region.sql")
    private Resource regionPopulatorMemory;
    @Value("classpath:db/migration/dev/V3.3__populate_district.sql")
    private Resource districtPopulatorMemory;
    @Value("classpath:db/migration/dev/V3.4__populate_city.sql")
    private Resource cityPopulatorMemory;
    @Value("classpath:db/migration/dev/V3.5__populate_countryside_postcode.sql")
    private Resource countrysidePostcodePopulatorMemory;
    @Value("classpath:db/migration/dev/V3.6__populate_tariff_grid.sql")
    private Resource tariffGridPopulatorMemory;
    @Value("classpath:db/migration/dev/V3.7_populate_city_postcode.sql")
    private Resource postcodePopulatorMemory;

    @Autowired
    public HibernateConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "flyway", initMethod = "migrate")
    @Profile("prod")
    public Flyway flyway() {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setLocations("db/migration/prod");
        flyway.setDataSource(dataSource());
        return flyway;
    }

    @Bean(name = "flyway", initMethod = "migrate")
    @Profile("dev")
    public Flyway flywayDevelopment() {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setLocations("db/migration/dev");
        flyway.setDataSource(dataSource());
        // uncomment if the problem with checksum or failed migration
//        flyway.repair();
        return flyway;
    }

    @Bean(name = "sessionFactory") @DependsOn("flyway")
    @Profile("prod")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(PACKAGE_TO_SCAN);
        sessionFactory.setHibernateProperties(hibernatePropertiesProduction());
        return sessionFactory;
    }

    @Bean(name = "sessionFactory") @DependsOn("flyway")
    @Profile("dev")
    public LocalSessionFactoryBean sessionFactoryDevelopment() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSourceDevelopment());
        sessionFactory.setPackagesToScan(PACKAGE_TO_SCAN);
        sessionFactory.setHibernateProperties(hibernatePropertiesDevelopment());
        return sessionFactory;
    }

    @Bean(name = "sessionFactory")
    @Profile("memory")
    public LocalSessionFactoryBean sessionFactoryInMemory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSourceInMemory());
        sessionFactory.setPackagesToScan(PACKAGE_TO_SCAN);
        sessionFactory.setHibernateProperties(hibernatePropertiesInMemory());
        return sessionFactory;
    }

    @Bean(name = "dataSource")
    @Profile("prod")
    public DataSource dataSource() {
        log.info("-----------------------------------------");
        log.info("----------ACTIVE SPRING PROFILE----------");
        log.info("-------------------PROD------------------");
        log.info("-----------------------------------------");
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
        log.info("-----------------------------------------");
        log.info("----------ACTIVE SPRING PROFILE----------");
        log.info("-------------------DEV-------------------");
        log.info("-----------------------------------------");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("dev.jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("dev.jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("dev.jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("dev.jdbc.password"));
        return dataSource;
    }

    @Bean(name = "dataSource")
    @Profile("memory")
    public DataSource dataSourceInMemory() {
        log.info("-----------------------------------------");
        log.info("----------ACTIVE SPRING PROFILE----------");
        log.info("------------------MEMORY-----------------");
        log.info("-----------------------------------------");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("memory.jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("memory.jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("memory.jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("memory.jdbc.password"));
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

    private Properties hibernatePropertiesInMemory() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("memory.hibernate.dialect"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("memory.hibernate.format_sql"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("memory.hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("memory.hibernate.hbm2ddl.auto"));
        return properties;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory s) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(s);
        return txManager;
    }

    // for in memory db, cuz trevis can't work with oracle, and we need CI
    @Bean
    @Autowired
    @Profile("memory")
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource, DatabasePopulator databasePopulator) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);
        return initializer;
    }

    @Bean(name = "databasePopulator")
    @Profile("memory")
    public DatabasePopulator databasePopulatorMemory() {
        log.info("DATABASE POPULATOR: memory");
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(countryPopulatorMemory);
        populator.addScript(regionPopulatorMemory);
        populator.addScript(districtPopulatorMemory);
        populator.addScript(cityPopulatorMemory);
        populator.addScript(countrysidePostcodePopulatorMemory);
        populator.addScript(tariffGridPopulatorMemory);
        populator.addScript(postcodePopulatorMemory);
        return populator;
    }

    @Bean(name = "databasePopulator")
    @Profile("prod")
    public DatabasePopulator databasePopulatorProduction() {
        log.info("DATABASE POPULATOR: prod");
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(countrysidePostcodePopulatorProd);
        populator.addScript(tariffGridPopulatorProd);
        populator.addScript(countryPopulatorProd);
        populator.addScript(regionPopulatorProd);
        populator.addScript(districtPopulatorProd);
        populator.addScript(cityPopulatorProd);
        populator.addScript(postcodePopulatorProd);
        return populator;
    }

}
