package com.methaltech.application.data.livedata.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.methaltech.application.data.livedata.repository",
        entityManagerFactoryRef = "thesunEntityManagerFactory",
        transactionManagerRef = "thesunTransactionManager")
public class thesunDataSourceConfiguration {
    @Bean
    @ConfigurationProperties("spring.datasource.livedata")
    public DataSourceProperties thesunDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("com.methaltech.application.data.livedata.configuration")
    public DataSource thesunDataSource() {
        return thesunDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    
    @Bean(name = "thesunEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean thesunEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(thesunDataSource())
                .packages( "com.methaltech.application.data.entity.livedata")
                .build();
    }

    
    @Bean(name = "thesunTransactionManager")
    public PlatformTransactionManager thesunTransactionManager(
            final @Qualifier("thesunEntityManagerFactory") LocalContainerEntityManagerFactoryBean thesunEntityManagerFactory) {
        return new JpaTransactionManager(thesunEntityManagerFactory.getObject());
    }
}
