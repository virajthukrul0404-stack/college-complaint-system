package com.college.complaint.dao.impl;

import com.college.complaint.dao.FeedbackDAO;
import com.college.complaint.model.Feedback;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FeedbackDAOImpl implements FeedbackDAO {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackDAOImpl.class);

    @Override
    public boolean create(Feedback feedback) {
        String sql = "INSERT INTO feedback (department_id, student_id, category, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, feedback.getDepartmentId());
            if (feedback.getStudentId() != null) {
                ps.setInt(2, feedback.getStudentId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, feedback.getCategory());
            ps.setInt(4, feedback.getRating());
            ps.setString(5, feedback.getComment());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        feedback.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating feedback", e);
            throw new RuntimeException("Database error saving feedback", e);
        }
        return false;
    }

    @Override
    public List<Feedback> findAll(int offset, int limit) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT f.id, f.department_id, f.student_id, d.name AS department_name, f.category, f.rating, f.comment, f.created_at " +
                     "FROM feedback f JOIN departments d ON f.department_id = d.id " +
                     "ORDER BY f.created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback fb = new Feedback();
                    fb.setId(rs.getInt("id"));
                    fb.setDepartmentId(rs.getInt("department_id"));
                    int sid = rs.getInt("student_id");
                    fb.setStudentId(rs.wasNull() ? null : sid);
                    fb.setDepartmentName(rs.getString("department_name"));
                    fb.setCategory(rs.getString("category"));
                    fb.setRating(rs.getInt("rating"));
                    fb.setComment(rs.getString("comment"));
                    fb.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(fb);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching feedback list", e);
            throw new RuntimeException("Database error fetching feedback", e);
        }
        return list;
    }

    @Override
    public List<Feedback> findByStudentId(int studentId, int offset, int limit) {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT f.id, f.department_id, f.student_id, d.name AS department_name, f.category, f.rating, f.comment, f.created_at " +
                     "FROM feedback f JOIN departments d ON f.department_id = d.id " +
                     "WHERE f.student_id = ? " +
                     "ORDER BY f.created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback fb = new Feedback();
                    fb.setId(rs.getInt("id"));
                    fb.setDepartmentId(rs.getInt("department_id"));
                    int sid = rs.getInt("student_id");
                    fb.setStudentId(rs.wasNull() ? null : sid);
                    fb.setDepartmentName(rs.getString("department_name"));
                    fb.setCategory(rs.getString("category"));
                    fb.setRating(rs.getInt("rating"));
                    fb.setComment(rs.getString("comment"));
                    fb.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(fb);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching feedback list for student: " + studentId, e);
        }
        return list;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM feedback";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error counting feedback", e);
        }
        return 0;
    }

    @Override
    public Map<String, Double> getAverageRatingByDepartment() {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = "SELECT d.name, AVG(CAST(f.rating AS DOUBLE)) AS avg_rating " +
                     "FROM departments d " +
                     "LEFT JOIN feedback f ON d.id = f.department_id " +
                     "GROUP BY d.id, d.name ORDER BY d.name";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double avg = rs.getDouble("avg_rating");
                // round to 1 decimal
                double rounded = Math.round(avg * 10.0) / 10.0;
                map.put(rs.getString("name"), rounded);
            }
        } catch (SQLException e) {
            logger.error("Error calculating average ratings by department", e);
        }
        return map;
    }

    @Override
    public Map<Integer, Integer> getRatingDistribution() {
        Map<Integer, Integer> dist = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) {
            dist.put(i, 0);
        }
        String sql = "SELECT rating, COUNT(*) AS cnt FROM feedback GROUP BY rating";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dist.put(rs.getInt("rating"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            logger.error("Error calculating rating distribution", e);
        }
        return dist;
    }

    @Override
    public double getOverallAverageRating() {
        String sql = "SELECT AVG(CAST(rating AS DOUBLE)) FROM feedback";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                double avg = rs.getDouble(1);
                return Math.round(avg * 10.0) / 10.0;
            }
        } catch (SQLException e) {
            logger.error("Error calculating overall average rating", e);
        }
        return 0.0;
    }
}
