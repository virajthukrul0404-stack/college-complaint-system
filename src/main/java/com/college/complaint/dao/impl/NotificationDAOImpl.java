package com.college.complaint.dao.impl;

import com.college.complaint.dao.NotificationDAO;
import com.college.complaint.model.Notification;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDAOImpl.class);

    @Override
    public boolean create(Notification notification) {
        String sql = "INSERT INTO notifications (student_id, complaint_id, message, is_read) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, notification.getStudentId());
            if (notification.getComplaintId() != null) {
                ps.setInt(2, notification.getComplaintId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, notification.getMessage());
            ps.setBoolean(4, notification.isRead());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        notification.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("Error creating notification for student {}: {}", notification.getStudentId(), e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<Notification> findByStudentId(int studentId, int limit) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT id, student_id, complaint_id, message, is_read, created_at " +
                     "FROM notifications WHERE student_id = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setId(rs.getInt("id"));
                    n.setStudentId(rs.getInt("student_id"));
                    int cId = rs.getInt("complaint_id");
                    n.setComplaintId(rs.wasNull() ? null : cId);
                    n.setMessage(rs.getString("message"));
                    n.setRead(rs.getBoolean("is_read"));
                    n.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(n);
                }
            }
        } catch (Exception e) {
            logger.error("Error finding notifications for student {}: {}", studentId, e.getMessage(), e);
        }
        return list;
    }

    @Override
    public int countUnreadByStudentId(int studentId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE student_id = ? AND is_read = FALSE";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            logger.error("Error counting unread notifications for student {}: {}", studentId, e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean markAllAsRead(int studentId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE student_id = ? AND is_read = FALSE";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() >= 0;
        } catch (Exception e) {
            logger.error("Error marking notifications as read for student {}: {}", studentId, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean markAsRead(int notificationId, int studentId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ? AND student_id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error marking notification {} as read: {}", notificationId, e.getMessage(), e);
        }
        return false;
    }
}
