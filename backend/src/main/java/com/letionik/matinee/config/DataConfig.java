package com.letionik.matinee.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Alexey Zyabkin on 20.09.2015.
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("com.letionik.matinee.repository")
public class DataConfig {
    private static final Logger log = LoggerFactory.getLogger(DataConfig.class);

    private static final String STAND_SYSTEM_PROPERTY_NAME = "stand";
    private static final String DEV_PROPERTIES_PATH_TEMPLATE = "/dev/{stand}.properties";
    private static final String PROP_DATABASE_DRIVER = "db.driver";
    private static final String PROP_DATABASE_PASSWORD = "db.password";
    private static final String PROP_DATABASE_URL = "db.url";
    private static final String PROP_DATABASE_USERNAME = "db.username";
    private static final String PROP_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROP_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROP_ENTITY_MANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
    private static final String PROP_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

    @Resource
    private Environment env;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty(PROP_DATABASE_DRIVER));
        boolean testPhase = !StringUtils.isEmpty(env.getProperty(PROP_DATABASE_URL));
        if (testPhase) {
            dataSource.setUrl(env.getRequiredProperty(PROP_DATABASE_URL));
            dataSource.setUsername(env.getRequiredProperty(PROP_DATABASE_USERNAME));
            dataSource.setPassword(env.getRequiredProperty(PROP_DATABASE_PASSWORD));
        } else {
            String stand = env.getRequiredProperty(STAND_SYSTEM_PROPERTY_NAME);
            try {
                Properties devProperties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(DEV_PROPERTIES_PATH_TEMPLATE.replace("{stand}", stand)));
                dataSource.setUrl(devProperties.getProperty(PROP_DATABASE_URL));
                dataSource.setUsername(devProperties.getProperty(PROP_DATABASE_USERNAME));
                dataSource.setPassword(devProperties.getProperty(PROP_DATABASE_PASSWORD));
            } catch (IOException e) {
                log.error("Can not read the file with developer's settings. Stand : " + stand, e);
            }
        }
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan(env.getRequiredProperty(PROP_ENTITY_MANAGER_PACKAGES_TO_SCAN));
        entityManagerFactoryBean.setJpaProperties(getHibernateProperties());
        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new AbstractConverter<Date, LocalDateTime>() {
            @Override
            protected LocalDateTime convert(Date source) {
                return LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
            }
        });
        modelMapper.addConverter(new AbstractConverter<LocalDateTime, Date>() {
            @Override
            protected Date convert(LocalDateTime source) {
                return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
            }
        });
        return modelMapper;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.put(PROP_HIBERNATE_DIALECT, env.getRequiredProperty(PROP_HIBERNATE_DIALECT));
        properties.put(PROP_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROP_HIBERNATE_SHOW_SQL));
        properties.put(PROP_HIBERNATE_HBM2DDL_AUTO, env.getRequiredProperty(PROP_HIBERNATE_HBM2DDL_AUTO));
        return properties;
    }
}