package com.college.complaint.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DbPool {

    private static final Logger logger = LoggerFactory.getLogger(DbPool.class);
    private static volatile HikariDataSource dataSource;

    private DbPool() {}

    public static HikariDataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DbPool.class) {
                if (dataSource == null) {
                    initDataSource();
                }
            }
        }
        return dataSource;
    }

    private static void initDataSource() {
        AppConfig.validateStartup();

        String driver = AppConfig.getDbDriver();
        String url = AppConfig.getDbUrl();
        String user = AppConfig.getDbUser();
        String password = AppConfig.getDbPassword();
        int poolSize = AppConfig.getDbPoolSize();

        logger.info("Initializing HikariCP: Driver={}, URL={}, User={}, PoolSize={}", 
                driver, url, user, poolSize);

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        // Cloud & container tuned parameters
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(Math.min(2, poolSize));
        config.setConnectionTimeout(20000); // 20 seconds
        config.setMaxLifetime(240000);       // 4 minutes (safely below MySQL wait_timeout)
        config.setKeepaliveTime(60000);      // 1 minute ping
        config.setIdleTimeout(60000);        // 1 minute
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("CollegeComplaintPool");

        // Retry loop with exponential backoff (handles cold starts and network delays)
        int maxRetries = 6;
        long delayMs = 2000;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                dataSource = new HikariDataSource(config);
                try (Connection conn = dataSource.getConnection()) {
                    logger.info("HikariCP connection pool initialized and verified successfully on attempt {}.", attempt);
                }
                return;
            } catch (Exception e) {
                if (dataSource != null && !dataSource.isClosed()) {
                    dataSource.close();
                    dataSource = null;
                }
                if (attempt == maxRetries) {
                    logger.error("Failed to connect to database after {} attempts over ~60s.", maxRetries, e);
                    throw new RuntimeException("Database pool initialization failed after retries: " + e.getMessage(), e);
                }
                logger.warn("Database connection attempt {}/{} failed ({}), retrying in {}ms...", 
                        attempt, maxRetries, e.getMessage(), delayMs);
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Database initialization interrupted", ie);
                }
                delayMs = Math.min(delayMs * 2, 16000);
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Shutting down HikariCP connection pool...");
            dataSource.close();
            dataSource = null;
        }
    }
}
