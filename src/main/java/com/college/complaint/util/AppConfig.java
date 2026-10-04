package com.college.complaint.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Properties;

public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final Properties props = new Properties();
    private static volatile boolean validated = false;
    private static String generatedAdminPassword = null;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            logger.warn("Could not load config.properties from classpath: {}", e.getMessage());
        }
    }

    public static String getAppEnv() {
        String env = System.getenv("APP_ENV");
        if (env != null && !env.trim().isEmpty()) {
            return env.trim().toLowerCase();
        }
        return props.getProperty("app.env", "development").trim().toLowerCase();
    }

    public static boolean isProduction() {
        return "production".equalsIgnoreCase(getAppEnv());
    }

    public static int getPort() {
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.trim().isEmpty()) {
            try {
                return Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {}
        }
        return Integer.parseInt(props.getProperty("server.port", "8080"));
    }

    public static String getDbDriver() {
        String driverEnv = System.getenv("DB_DRIVER");
        if (driverEnv != null && !driverEnv.trim().isEmpty()) {
            return driverEnv.trim();
        }
        String url = getDbUrl();
        if (url != null && url.startsWith("jdbc:mysql:")) {
            return "com.mysql.cj.jdbc.Driver";
        }
        return props.getProperty("db.driver", "org.h2.Driver");
    }

    public static String getDbUrl() {
        String urlEnv = System.getenv("DB_URL");
        if (urlEnv != null && !urlEnv.trim().isEmpty()) {
            return urlEnv.trim();
        }
        return props.getProperty("db.url", "jdbc:h2:./db/complaint_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE");
    }

    public static String getDbUser() {
        String userEnv = System.getenv("DB_USER");
        if (userEnv != null && !userEnv.trim().isEmpty()) {
            return userEnv.trim();
        }
        return props.getProperty("db.user", "sa");
    }

    public static String getDbPassword() {
        String passEnv = System.getenv("DB_PASSWORD");
        if (passEnv != null) {
            return passEnv;
        }
        return props.getProperty("db.password", "");
    }

    public static int getDbPoolSize() {
        String poolEnv = System.getenv("DB_POOL_SIZE");
        if (poolEnv != null && !poolEnv.trim().isEmpty()) {
            try {
                return Integer.parseInt(poolEnv.trim());
            } catch (NumberFormatException ignored) {}
        }
        int defaultSize = isProduction() ? 5 : 10;
        return Integer.parseInt(props.getProperty("db.pool.maximumPoolSize", String.valueOf(defaultSize)));
    }

    public static synchronized String getAdminInitialPassword() {
        String adminPass = System.getenv("ADMIN_INITIAL_PASSWORD");
        if (adminPass != null && !adminPass.trim().isEmpty()) {
            return adminPass.trim();
        }
        if (isProduction()) {
            if (generatedAdminPassword == null) {
                generatedAdminPassword = generateSecureRandomPassword(16);
                logger.warn("========================================================================");
                logger.warn(" [SECURITY WARNING] ADMIN_INITIAL_PASSWORD not set in production!");
                logger.warn(" Generated secure random password for 'superadmin':");
                logger.warn(" >>> {} <<<", generatedAdminPassword);
                logger.warn(" Please log in and change this password immediately!");
                logger.warn("========================================================================");
            }
            return generatedAdminPassword;
        }
        return "Admin@12345";
    }

    public static boolean isSeedDemoData() {
        String seedEnv = System.getenv("SEED_DEMO_DATA");
        if (seedEnv != null && !seedEnv.trim().isEmpty()) {
            return "true".equalsIgnoreCase(seedEnv.trim());
        }
        return !isProduction(); // True in dev, false in prod
    }

    public static String getStorageMode() {
        String modeEnv = System.getenv("STORAGE_MODE");
        if (modeEnv != null && !modeEnv.trim().isEmpty()) {
            return modeEnv.trim().toLowerCase();
        }
        return isProduction() ? "db" : props.getProperty("storage.mode", "disk").trim().toLowerCase();
    }

    public static boolean isDbStorage() {
        return "db".equalsIgnoreCase(getStorageMode());
    }

    public static String getAllowedEmailDomain() {
        String domainEnv = System.getenv("ALLOWED_EMAIL_DOMAIN");
        if (domainEnv != null && !domainEnv.trim().isEmpty()) {
            return domainEnv.trim().toLowerCase();
        }
        String prop = props.getProperty("auth.allowed_email_domain", "");
        return prop.trim().isEmpty() ? null : prop.trim().toLowerCase();
    }

    public static void validateStartup() {
        if (validated) return;

        logger.info("Application starting in environment: [{}]", getAppEnv());
        logger.info("Storage mode: [{}], Seed demo data: [{}]", getStorageMode(), isSeedDemoData());

        if (isProduction()) {
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl == null || dbUrl.trim().isEmpty()) {
                String msg = "FATAL: In production (APP_ENV=production), required environment variable 'DB_URL' is missing! " +
                        "Render requires an external MySQL database connection string.";
                logger.error(msg);
                throw new IllegalStateException(msg);
            }

            String dbUser = System.getenv("DB_USER");
            if (dbUser == null || dbUser.trim().isEmpty()) {
                String msg = "FATAL: In production (APP_ENV=production), required environment variable 'DB_USER' is missing!";
                logger.error(msg);
                throw new IllegalStateException(msg);
            }

            // Ensure password is not null (can be empty string on some local setups, but in prod must be provided)
            if (System.getenv("DB_PASSWORD") == null) {
                logger.warn("Notice: DB_PASSWORD environment variable is not set. Assuming empty password.");
            }

            // Trigger admin password generation banner if needed
            getAdminInitialPassword();
        }

        validated = true;
    }

    private static String generateSecureRandomPassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // For unit testing
    public static void resetValidation() {
        validated = false;
        generatedAdminPassword = null;
    }
}
