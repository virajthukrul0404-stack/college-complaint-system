package com.college.complaint.dao.impl;

import com.college.complaint.dao.AdminDAO;
import com.college.complaint.model.Admin;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImpl implements AdminDAO {

    private static final Logger logger = LoggerFactory.getLogger(AdminDAOImpl.class);

    @Override
    public Admin findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, full_name, email, role, created_at FROM admins WHERE username = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding admin by username: " + username, e);
            throw new RuntimeException("Database error finding admin", e);
        }
        return null;
    }

    @Override
    public Admin findById(int id) {
        String sql = "SELECT id, username, password_hash, full_name, email, role, created_at FROM admins WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding admin by id: " + id, e);
            throw new RuntimeException("Database error finding admin", e);
        }
        return null;
    }

    @Override
    public List<Admin> findAll() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, full_name, email, role, created_at FROM admins ORDER BY id ASC";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToAdmin(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all admins", e);
            throw new RuntimeException("Database error fetching admins", e);
        }
        return list;
    }

    @Override
    public boolean createAdmin(Admin admin) {
        String sql = "INSERT INTO admins (username, password_hash, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPasswordHash());
            ps.setString(3, admin.getFullName());
            ps.setString(4, admin.getEmail());
            ps.setString(5, admin.getRole() != null ? admin.getRole() : "ADMIN");
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        admin.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating admin: " + admin.getUsername(), e);
            throw new RuntimeException("Database error creating admin", e);
        }
        return false;
    }

    @Override
    public boolean updatePassword(int adminId, String newPasswordHash) {
        String sql = "UPDATE admins SET password_hash = ? WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating password for admin: " + adminId, e);
            throw new RuntimeException("Database error updating password", e);
        }
    }

    @Override
    public void recordLoginAttempt(String ipAddress, String username, boolean success) {
        String sql = "INSERT INTO login_attempts (ip_address, username, attempt_time, success) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ipAddress);
            ps.setString(2, username);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setBoolean(4, success);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.warn("Could not record login attempt for {}: {}", username, e.getMessage());
        }
    }

    @Override
    public int getFailedAttemptsCount(String ipAddress, String username, int windowMinutes) {
        String sql = "SELECT COUNT(*) FROM login_attempts WHERE ip_address = ? AND username = ? AND success = FALSE AND attempt_time >= ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp cutoff = Timestamp.from(Instant.now().minus(windowMinutes, ChronoUnit.MINUTES));
            ps.setString(1, ipAddress);
            ps.setString(2, username);
            ps.setTimestamp(3, cutoff);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.warn("Error getting failed attempts for {}: {}", username, e.getMessage());
        }
        return 0;
    }

    @Override
    public void clearLoginAttempts(String ipAddress, String username) {
        String sql = "DELETE FROM login_attempts WHERE ip_address = ? AND username = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ipAddress);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.warn("Error clearing login attempts for {}: {}", username, e.getMessage());
        }
    }

    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt("id"));
        admin.setUsername(rs.getString("username"));
        admin.setPasswordHash(rs.getString("password_hash"));
        admin.setFullName(rs.getString("full_name"));
        admin.setEmail(rs.getString("email"));
        admin.setRole(rs.getString("role"));
        admin.setCreatedAt(rs.getTimestamp("created_at"));
        return admin;
    }
}
