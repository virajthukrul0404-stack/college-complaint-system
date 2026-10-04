package com.college.complaint.dao.impl;

import com.college.complaint.dao.PasswordResetDAO;
import com.college.complaint.model.PasswordReset;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PasswordResetDAOImpl implements PasswordResetDAO {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetDAOImpl.class);

    @Override
    public boolean saveResetCode(PasswordReset reset) {
        // Invalidate old codes first
        invalidateOldCodes(reset.getStudentId());

        String sql = "INSERT INTO password_resets (student_id, code_hash, expires_at, attempts, used) VALUES (?, ?, ?, 0, FALSE)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reset.getStudentId());
            ps.setString(2, reset.getCodeHash());
            ps.setTimestamp(3, reset.getExpiresAt());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error saving password reset code: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public PasswordReset findActiveByStudentId(int studentId) {
        String sql = "SELECT id, student_id, code_hash, expires_at, attempts, used, created_at " +
                     "FROM password_resets " +
                     "WHERE student_id = ? AND used = FALSE AND expires_at > CURRENT_TIMESTAMP AND attempts < 5 " +
                     "ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PasswordReset pr = new PasswordReset();
                    pr.setId(rs.getInt("id"));
                    pr.setStudentId(rs.getInt("student_id"));
                    pr.setCodeHash(rs.getString("code_hash"));
                    pr.setExpiresAt(rs.getTimestamp("expires_at"));
                    pr.setAttempts(rs.getInt("attempts"));
                    pr.setUsed(rs.getBoolean("used"));
                    pr.setCreatedAt(rs.getTimestamp("created_at"));
                    return pr;
                }
            }
        } catch (Exception e) {
            logger.error("Error finding active password reset for student {}: {}", studentId, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean incrementAttempts(int id) {
        String sql = "UPDATE password_resets SET attempts = attempts + 1 WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error incrementing reset code attempts: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean markUsed(int id) {
        String sql = "UPDATE password_resets SET used = TRUE WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error marking reset code as used: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void invalidateOldCodes(int studentId) {
        String sql = "UPDATE password_resets SET used = TRUE WHERE student_id = ? AND used = FALSE";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.warn("Error invalidating old reset codes: {}", e.getMessage());
        }
    }
}
