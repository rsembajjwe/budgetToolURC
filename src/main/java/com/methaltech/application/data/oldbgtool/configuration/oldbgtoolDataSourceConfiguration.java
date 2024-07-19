package com.methaltech.application.data.oldbgtool.configuration;

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
@EnableJpaRepositories(basePackages = "com.methaltech.application.data.oldbgtool.repository",
        entityManagerFactoryRef = "oldbgtoolEntityManagerFactory",
        transactionManagerRef = "oldbgtoolTransactionManager")
public class oldbgtoolDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.oldbgtool")
    public DataSourceProperties oldbgtoolDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("com.methaltech.application.data.oldbgtool.configuration")
    public DataSource oldbgtoolDataSource() {
        return oldbgtoolDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean(name = "oldbgtoolEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean oldbgtoolEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(oldbgtoolDataSource())
                .packages("com.methaltech.application.data.entity.oldbgtool")
                .build();
    }

    @Bean(name = "oldbgtoolTransactionManager")
    public PlatformTransactionManager oldbgtoolTransactionManager(
            final @Qualifier("oldbgtoolEntityManagerFactory") LocalContainerEntityManagerFactoryBean oldbgtoolEntityManagerFactory) {
        return new JpaTransactionManager(oldbgtoolEntityManagerFactory.getObject());
    }
}
