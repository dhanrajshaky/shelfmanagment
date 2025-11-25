package com.example.library.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DataSourceConfig {

    // Create database if it doesn't exist and return a Hikari DataSource
    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        String host = env.getProperty("MYSQL_HOST", "localhost");
        String port = env.getProperty("MYSQL_PORT", "3306");
        String db = env.getProperty("MYSQL_DB", "libraryshelf");
        String user = env.getProperty("MYSQL_USER", "root");
        String pass = env.getProperty("MYSQL_PASSWORD", "");
        
        boolean isPlanetScale = host.contains("tidbcloud.com") || host.contains("planetscale.com");
        
        // For PlanetScale/TiDB, use SSL by default
        String params = isPlanetScale ? "?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true" 
                                      : "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        try {
            // Try to create database for local MySQL only (skip for cloud-hosted databases)
            if (!isPlanetScale) {
                String jdbcNoDb = String.format("jdbc:mysql://%s:%s/%s", host, port, "");
                if (!jdbcNoDb.endsWith("/")) jdbcNoDb = jdbcNoDb + "/";
                jdbcNoDb += params;

                try (Connection c = DriverManager.getConnection(jdbcNoDb, user, pass);
                     Statement s = c.createStatement()) {
                    String sql = String.format("CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", db);
                    s.executeUpdate(sql);
                } catch (SQLException e) {
                    // Log warning but continue - database may already exist
                    System.err.println("Warning: Could not create database: " + e.getMessage());
                }
            }

            String jdbc = String.format("jdbc:mysql://%s:%s/%s%s", host, port, db, params);

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(jdbc);
            cfg.setUsername(user);
            cfg.setPassword(pass);
            cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
            cfg.setMaximumPoolSize(10);
            cfg.setMinimumIdle(2);
            cfg.setConnectionTimeout(10000); // 10 second timeout for remote connections
            cfg.setIdleTimeout(600000); // 10 minutes
            cfg.setMaxLifetime(1800000); // 30 minutes
            
            // For PlanetScale/TiDB cloud databases
            if (isPlanetScale) {
                cfg.addDataSourceProperty("serverTimezone", "UTC");
                cfg.addDataSourceProperty("allowPublicKeyRetrieval", "true");
            }

            return new HikariDataSource(cfg);
        } catch (Exception e) {
            System.err.println("Error connecting to MySQL, falling back to H2 in-memory database");
            System.err.println("Error details: " + e.getMessage());
            
            // Fallback to H2 in-memory database
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl("jdbc:h2:mem:librarydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            cfg.setUsername("root");
            cfg.setPassword("");
            cfg.setDriverClassName("org.h2.Driver");
            cfg.setMaximumPoolSize(10);
            
            return new HikariDataSource(cfg);
        }
    }
}
