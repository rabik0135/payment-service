package com.rabinchuk.paymentservice.config;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LiquibaseConfig {

    @Value("${spring.data.mongodb.uri}")
    private String connectionUrl;

    @Value("${spring.liquibase.change-log}")
    private String changeLog;

    @Bean
    public Liquibase liquibase() throws Exception {
        MongoLiquibaseDatabase database = (MongoLiquibaseDatabase) DatabaseFactory.getInstance()
                .openDatabase(
                        connectionUrl,
                        null, null, null, null,
                        new ClassLoaderResourceAccessor()
                );

        Liquibase liquibase = new Liquibase(
                changeLog,
                new ClassLoaderResourceAccessor(),
                database
        );

        liquibase.update("");
        return liquibase;
    }
}
