package com.methaltech.application.data.bgtool.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.methaltech.application.data.bgtool.repository",
        entityManagerFactoryRef = "bgtoolEntityManagerFactory",
        transactionManagerRef = "bgtoolTransactionManager")
public class bgtoolDataSourceConfiguration {
    @Bean
    @Primary    
    @ConfigurationProperties("spring.datasource.bgtool")
    public DataSourceProperties bgtoolDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary    
    @ConfigurationProperties("com.methaltech.application.data.bgtool.configuration")
    public DataSource bgtoolDataSource() {
        return bgtoolDataSourceProperties().initializeDataSourceBuilder()
                .type( HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "bgtoolEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean bgtoolEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(bgtoolDataSource())
                .packages( "com.methaltech.application.data.entity.bgtool")
                .build();
    }

    @Primary
    @Bean(name = "bgtoolTransactionManager")
    public PlatformTransactionManager bgtoolTransactionManager(
            final @Qualifier("bgtoolEntityManagerFactory") LocalContainerEntityManagerFactoryBean bgtoolEntityManagerFactory) {
        return new JpaTransactionManager(bgtoolEntityManagerFactory.getObject());
    }
}
