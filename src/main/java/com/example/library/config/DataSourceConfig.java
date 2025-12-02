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
        // Support platform-provided datasource URL (Railway plugin provides SPRING_DATASOURCE_URL)
        String springUrl = env.getProperty("SPRING_DATASOURCE_URL");
        String springUser = env.getProperty("SPRING_DATASOURCE_USERNAME");
        String springPass = env.getProperty("SPRING_DATASOURCE_PASSWORD");

        String host = env.getProperty("MYSQL_HOST", "localhost");
        String port = env.getProperty("MYSQL_PORT", "3306");
        String db = env.getProperty("MYSQL_DB", "libraryshelf");
        String user = env.getProperty("MYSQL_USER", "root");
        String pass = env.getProperty("MYSQL_PASSWORD", "");
        String activeProfile = env.getProperty("SPRING_PROFILES_ACTIVE", "");

        // If platform provided SPRING_DATASOURCE_URL, prefer that and override user/pass if provided
        boolean usingSpringDatasourceUrl = springUrl != null && !springUrl.isBlank();
        if (usingSpringDatasourceUrl) {
            if (springUser != null && !springUser.isBlank()) user = springUser;
            if (springPass != null && !springPass.isBlank()) pass = springPass;
        }

        // Safety check: when the mysql profile is active and SPRING_DATASOURCE_URL isn't provided,
        // ensure the host isn't left at the default `localhost` (common mistake when env vars are not configured).
        if (!usingSpringDatasourceUrl && "mysql".equalsIgnoreCase(activeProfile) && (host == null || "localhost".equals(host) || "127.0.0.1".equals(host))) {
            String hint = "MYSQL_HOST is not configured (using default 'localhost'). " +
                "Set the environment variables MYSQL_HOST, MYSQL_PORT, MYSQL_DB, MYSQL_USER, and MYSQL_PASSWORD in your Railway/Render/service dashboard to point to your PlanetScale/TiDB instance, or provide SPRING_DATASOURCE_URL.";
            System.err.println("Configuration error: " + hint);
            throw new IllegalStateException(hint);
        }

        boolean isPlanetScale = host.contains("tidbcloud.com") || host.contains("planetscale.com");

        // For PlanetScale/TiDB (cloud) require SSL and use recommended JDBC params
        // sslMode=REQUIRED avoids common SSL handshake issues on cloud providers
        String params = isPlanetScale
            ? "?useSSL=true&sslMode=REQUIRED&serverTimezone=UTC&allowPublicKeyRetrieval=true"
            : "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        try {
            // Try to create database for local MySQL only (skip for cloud-hosted databases)
            if (!isPlanetScale && !usingSpringDatasourceUrl) {
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

            String jdbc;
            if (usingSpringDatasourceUrl) {
                jdbc = springUrl;
            } else {
                jdbc = String.format("jdbc:mysql://%s:%s/%s%s", host, port, db, params);
            }

            // Log the JDBC URL without credentials so platform logs show which host/DB we're attempting
            try {
                System.out.println("Initializing MySQL DataSource, jdbcUrl=" + (usingSpringDatasourceUrl ? springUrl : jdbc));
            } catch (Exception ignored) {}

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
            
            // For PlanetScale/TiDB cloud databases: make sure driver-level props are set
            if (isPlanetScale) {
                cfg.addDataSourceProperty("serverTimezone", "UTC");
                cfg.addDataSourceProperty("allowPublicKeyRetrieval", "true");
                cfg.addDataSourceProperty("useSSL", "true");
                cfg.addDataSourceProperty("sslMode", "REQUIRED");
            }

            return new HikariDataSource(cfg);
        } catch (Exception e) {
            String msg = "Failed to initialize MySQL DataSource for host=" + host + " db=" + db + ". Failing fast (no H2 fallback).";
            System.err.println(msg);
            e.printStackTrace(System.err);
            throw new IllegalStateException(msg, e);
        }
    }
}
