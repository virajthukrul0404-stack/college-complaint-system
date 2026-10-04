package com.college.complaint.dao.impl;

import com.college.complaint.dao.RememberTokenDAO;
import com.college.complaint.model.RememberToken;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RememberTokenDAOImpl implements RememberTokenDAO {

    private static final Logger logger = LoggerFactory.getLogger(RememberTokenDAOImpl.class);

    @Override
    public boolean saveToken(RememberToken token) {
        String sql = "INSERT INTO remember_tokens (student_id, token_hash, expires_at, user_agent) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.getStudentId());
            ps.setString(2, token.getTokenHash());
            ps.setTimestamp(3, token.getExpiresAt());
            ps.setString(4, token.getUserAgent());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error saving remember token: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public RememberToken findByTokenHash(String tokenHash) {
        String sql = "SELECT id, student_id, token_hash, expires_at, user_agent, created_at " +
                     "FROM remember_tokens WHERE token_hash = ? AND expires_at > CURRENT_TIMESTAMP";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RememberToken t = new RememberToken();
                    t.setId(rs.getInt("id"));
                    t.setStudentId(rs.getInt("student_id"));
                    t.setTokenHash(rs.getString("token_hash"));
                    t.setExpiresAt(rs.getTimestamp("expires_at"));
                    t.setUserAgent(rs.getString("user_agent"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    return t;
                }
            }
        } catch (Exception e) {
            logger.error("Error finding remember token: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean deleteByStudentId(int studentId) {
        String sql = "DELETE FROM remember_tokens WHERE student_id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error deleting remember tokens for student {}: {}", studentId, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteByTokenHash(String tokenHash) {
        String sql = "DELETE FROM remember_tokens WHERE token_hash = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error deleting remember token: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void cleanExpiredTokens() {
        String sql = "DELETE FROM remember_tokens WHERE expires_at <= CURRENT_TIMESTAMP";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception e) {
            logger.warn("Error cleaning expired tokens: {}", e.getMessage());
        }
    }
}
