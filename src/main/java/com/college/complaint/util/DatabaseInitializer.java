package com.college.complaint.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    public static synchronized void initialize() {
        try (Connection conn = DbPool.getConnection()) {
            logger.info("Verifying database schema and migrations...");
            executeSqlScript(conn, "db/schema.sql");

            // Safe column migrations
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE complaints ADD COLUMN IF NOT EXISTS student_id INT NULL");
                stmt.execute("ALTER TABLE feedback ADD COLUMN IF NOT EXISTS student_id INT NULL");
            } catch (Exception ex) {
                logger.debug("Migration notice: {}", ex.getMessage());
            }

            // 1. Ensure essential departments exist
            if (isTableEmpty(conn, "departments")) {
                logger.info("Departments table is empty. Seeding core academic departments...");
                seedCoreDepartments(conn);
            }

            // 2. Ensure initial administrator exists
            if (isTableEmpty(conn, "admins")) {
                logger.info("Admins table is empty. Creating initial superadmin account...");
                seedInitialAdmin(conn);
            }

            // 3. Demo Data (students and sample complaints) only if explicitly enabled
            if (AppConfig.isSeedDemoData()) {
                if (isTableEmpty(conn, "students") || isTableEmpty(conn, "complaints")) {
                    logger.info("Demo data enabled (SEED_DEMO_DATA=true). Seeding demo students and complaints...");
                    executeSqlScript(conn, "db/seed.sql");
                }
            } else {
                logger.info("Production mode: SEED_DEMO_DATA is false. Skipping demo students and sample complaints.");
            }

            logger.info("Database schema verification and bootstrapping complete.");
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization error", e);
        }
    }

    private static void seedCoreDepartments(Connection conn) {
        String sql = "INSERT INTO departments (id, code, name, description) VALUES " +
                "(1, 'CSE', 'Computer Science & Engineering', 'Department of Computer Science and Engineering'), " +
                "(2, 'IT', 'Information Technology', 'Department of Information Technology'), " +
                "(3, 'MECH', 'Mechanical Engineering', 'Department of Mechanical Engineering'), " +
                "(4, 'CIVIL', 'Civil Engineering', 'Department of Civil Engineering'), " +
                "(5, 'ECE', 'Electronics & Communication', 'Department of Electronics and Communication Engineering'), " +
                "(6, 'ADMIN', 'Campus Administration', 'Office of Campus Administration & Estate Maintenance')";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            logger.error("Failed to seed core departments", e);
        }
    }

    private static void seedInitialAdmin(Connection conn) {
        String rawPassword = AppConfig.getAdminInitialPassword();
        String hash = PasswordUtil.hashPassword(rawPassword);
        String sql = "INSERT INTO admins (username, password_hash, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "superadmin");
            pstmt.setString(2, hash);
            pstmt.setString(3, "Chief Administrator");
            pstmt.setString(4, "admin@campus.edu");
            pstmt.setString(5, "SUPER_ADMIN");
            pstmt.executeUpdate();
            logger.info("Initial superadmin created successfully (username: 'superadmin').");
        } catch (Exception e) {
            logger.error("Failed to create initial superadmin", e);
        }
    }

    private static boolean checkTablesExist(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, null, "COMPLAINTS", new String[]{"TABLE"})) {
                if (rs.next()) return true;
            }
            try (ResultSet rs = meta.getTables(null, null, "complaints", new String[]{"TABLE"})) {
                if (rs.next()) return true;
            }
            return false;
        } catch (Exception e) {
            logger.warn("Could not check metadata for tables, will attempt fallback check", e);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1 FROM complaints LIMIT 1")) {
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    private static boolean isTableEmpty(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception e) {
            logger.warn("Error checking table {}: {}", tableName, e.getMessage());
        }
        return true;
    }

    private static void executeSqlScript(Connection conn, String scriptPath) throws Exception {
        List<String> statements = readSqlStatements(scriptPath);
        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        stmt.execute(trimmed);
                    } catch (Exception e) {
                        logger.warn("SQL statement warning/error [{}]: {}", trimmed.substring(0, Math.min(60, trimmed.length())), e.getMessage());
                    }
                }
            }
        }
    }

    private static List<String> readSqlStatements(String path) throws Exception {
        List<String> list = new ArrayList<>();
        BufferedReader reader = null;

        File file = new File(path);
        if (file.exists()) {
            reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
        } else {
            InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream(path);
            if (in != null) {
                reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            } else {
                // Try looking in current working directory or db/
                File fallback = new File("./" + path);
                if (fallback.exists()) {
                    reader = new BufferedReader(new FileReader(fallback, StandardCharsets.UTF_8));
                }
            }
        }

        if (reader == null) {
            throw new IllegalStateException("Could not find SQL script: " + path);
        }

        try (BufferedReader br = reader) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.startsWith("#") || trimmed.isEmpty()) {
                    continue;
                }
                sb.append(line).append(" ");
                if (trimmed.endsWith(";")) {
                    list.add(sb.toString().trim().replaceAll(";$", ""));
                    sb.setLength(0);
                }
            }
            if (!sb.toString().trim().isEmpty()) {
                list.add(sb.toString().trim());
            }
        }
        return list;
    }
}
