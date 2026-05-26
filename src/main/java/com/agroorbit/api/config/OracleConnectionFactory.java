package com.agroorbit.api.config;

import com.agroorbit.api.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class OracleConnectionFactory {

    @Value("${oracle.datasource.url}")
    private String url;

    @Value("${oracle.datasource.username}")
    private String username;

    @Value("${oracle.datasource.password}")
    private String password;

    @Value("${oracle.datasource.driver-class-name}")
    private String driverClassName;

    public Connection getConnection() {
        try {
            Class.forName(driverClassName);
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new DatabaseException("Erro ao conectar no banco Oracle", e);
        }
    }
}