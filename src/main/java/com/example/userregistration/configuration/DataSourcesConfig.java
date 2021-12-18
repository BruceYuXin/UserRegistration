package com.example.userregistration.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourcesConfig {

    @Bean(name="dbSource")
    @ConfigurationProperties(prefix = "spring.datasource.one")
    @Primary
    DataSource dbSource(){
        return DataSourceBuilder.create().build();
    }
}
