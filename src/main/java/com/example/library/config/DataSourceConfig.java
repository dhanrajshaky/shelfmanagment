package com.example.library.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
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
    public DataSource dataSource(Environment env) throws SQLException {
        String host = env.getProperty("MYSQL_HOST", "localhost");
        String port = env.getProperty("MYSQL_PORT", "3306");
        String db = env.getProperty("MYSQL_DB", "libraryshelf");
        String user = env.getProperty("MYSQL_USER", "root");
        String pass = env.getProperty("MYSQL_PASSWORD", "");
        String params = "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

        String jdbcNoDb = String.format("jdbc:mysql://%s:%s/%s", host, port, "");
        // Some drivers require a trailing slash for the no-db URL
        if (!jdbcNoDb.endsWith("/")) jdbcNoDb = jdbcNoDb + "/";
        jdbcNoDb += params;

        // Attempt to create database if missing. This requires the user to have
        // privileges to create databases on the server.
        try (Connection c = DriverManager.getConnection(jdbcNoDb, user, pass);
             Statement s = c.createStatement()) {
            String sql = String.format("CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", db);
            s.executeUpdate(sql);
        } catch (SQLException e) {
            // If creation failed, rethrow with context — app may still start if DB exists
            throw new SQLException("Unable to create or access database '" + db + "' using provided credentials", e);
        }

        String jdbc = String.format("jdbc:mysql://%s:%s/%s%s", host, port, db, params);

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbc);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);

        return new HikariDataSource(cfg);
    }
}
