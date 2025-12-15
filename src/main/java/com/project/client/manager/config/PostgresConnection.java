package com.project.client.manager.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class PostgresConnection {

    @Value("${spring.datasource.url}")
    private  String url;

    @Value("${spring.datasource.username}")
    private  String userName;

    @Value("${spring.datasource.password}")
    private  String password;

    public Connection connect() {
        Connection connection;
        try{
           connection = DriverManager.getConnection(url, userName, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }
}
