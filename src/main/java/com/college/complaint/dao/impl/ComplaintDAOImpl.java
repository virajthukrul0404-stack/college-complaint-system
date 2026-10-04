package com.college.complaint.dao.impl;

import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.model.Complaint;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ComplaintDAOImpl implements ComplaintDAO {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintDAOImpl.class);

    private static final String BASE_SELECT =
            "SELECT c.id, c.tracking_id, c.student_id, c.student_name, c.roll_number, c.email, c.department_id, " +
            "d.name AS department_name, d.code AS department_code, c.category, c.priority, " +
            "c.subject, c.description, c.is_anonymous, c.status, c.attachment_path, " +
            "c.internal_notes, c.public_remark, c.assigned_to, c.is_public, c.deleted_at, " +
            "c.created_at, c.updated_at " +
            "FROM complaints c " +
            "JOIN departments d ON c.department_id = d.id ";

    @Override
    public boolean create(Complaint c) {
        String sql = "INSERT INTO complaints (tracking_id, student_id, student_name, roll_number, email, department_id, " +
                "category, priority, subject, description, is_anonymous, status, attachment_path, is_public) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getTrackingId());
            if (c.getStudentId() != null) {
                ps.setInt(2, c.getStudentId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, c.getStudentName());
            ps.setString(4, c.getRollNumber());
            ps.setString(5, c.getEmail());
            ps.setInt(6, c.getDepartmentId());
            ps.setString(7, c.getCategory());
            ps.setString(8, c.getPriority() != null ? c.getPriority() : "Medium");
            ps.setString(9, c.getSubject());
            ps.setString(10, c.getDescription());
            ps.setBoolean(11, c.isAnonymous());
            ps.setString(12, c.getStatus() != null ? c.getStatus() : "Submitted");
            ps.setString(13, c.getAttachmentPath());
            ps.setBoolean(14, c.isPublic());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setId(rs.getInt(1));
                    }
                }
                // Also insert initial status log
                insertStatusLog(conn, c.getId(), null, c.getStatus() != null ? c.getStatus() : "Submitted",
                        c.isAnonymous() ? "Anonymous Student" : c.getStudentName(), "Complaint filed.");
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating complaint with tracking ID: " + c.getTrackingId(), e);
            throw new RuntimeException("Database error saving complaint", e);
        }
        return false;
    }

    @Override
    public Complaint findById(int id) {
        String sql = BASE_SELECT + "WHERE c.id = ? AND c.deleted_at IS NULL";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComplaint(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding complaint by id: " + id, e);
            throw new RuntimeException("Database error fetching complaint", e);
        }
        return null;
    }

    @Override
    public Complaint findByTrackingId(String trackingId) {
        String sql = BASE_SELECT + "WHERE c.tracking_id = ? AND c.deleted_at IS NULL";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingId != null ? trackingId.trim() : "");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComplaint(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding complaint by trackingId: " + trackingId, e);
            throw new RuntimeException("Database error fetching complaint", e);
        }
        return null;
    }

    @Override
    public Complaint findByTrackingIdAndEmail(String trackingId, String email) {
        String sql = BASE_SELECT + "WHERE c.tracking_id = ? AND LOWER(c.email) = LOWER(?) AND c.deleted_at IS NULL";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingId != null ? trackingId.trim() : "");
            ps.setString(2, email != null ? email.trim() : "");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComplaint(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding complaint by trackingId and email: " + trackingId, e);
            throw new RuntimeException("Database error fetching complaint", e);
        }
        return null;
    }

    @Override
    public List<Complaint> searchAndFilter(
            String search,
            String status,
            Integer departmentId,
            String category,
            String priority,
            String startDate,
            String endDate,
            String sortBy,
            String sortOrder,
            int offset,
            int limit
    ) {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE c.deleted_at IS NULL ");

        List<Object> params = new ArrayList<>();
        buildWhereClause(sql, params, search, status, departmentId, category, priority, startDate, endDate);

        // Sorting
        String orderCol = "c.created_at";
        if ("priority".equalsIgnoreCase(sortBy)) orderCol = "c.priority";
        else if ("status".equalsIgnoreCase(sortBy)) orderCol = "c.status";
        else if ("department".equalsIgnoreCase(sortBy)) orderCol = "d.name";
        else if ("trackingId".equalsIgnoreCase(sortBy)) orderCol = "c.tracking_id";

        String dir = "ASC".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC";
        sql.append("ORDER BY ").append(orderCol).append(" ").append(dir).append(" ");
        sql.append("LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching complaints", e);
            throw new RuntimeException("Database error filtering complaints", e);
        }
        return list;
    }

    @Override
    public int countSearchAndFilter(
            String search,
            String status,
            Integer departmentId,
            String category,
            String priority,
            String startDate,
            String endDate
    ) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM complaints c JOIN departments d ON c.department_id = d.id WHERE c.deleted_at IS NULL ");
        List<Object> params = new ArrayList<>();
        buildWhereClause(sql, params, search, status, departmentId, category, priority, startDate, endDate);

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting filtered complaints", e);
        }
        return 0;
    }

    private void buildWhereClause(StringBuilder sql, List<Object> params,
                                  String search, String status, Integer departmentId,
                                  String category, String priority, String startDate, String endDate) {
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(c.tracking_id) LIKE ? OR LOWER(c.subject) LIKE ? OR LOWER(c.student_name) LIKE ? OR LOWER(c.roll_number) LIKE ?) ");
            String s = "%" + search.trim().toLowerCase() + "%";
            params.add(s);
            params.add(s);
            params.add(s);
            params.add(s);
        }
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND c.status = ? ");
            params.add(status.trim());
        }
        if (departmentId != null && departmentId > 0) {
            sql.append("AND c.department_id = ? ");
            params.add(departmentId);
        }
        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            sql.append("AND c.category = ? ");
            params.add(category.trim());
        }
        if (priority != null && !priority.trim().isEmpty() && !"ALL".equalsIgnoreCase(priority)) {
            sql.append("AND c.priority = ? ");
            params.add(priority.trim());
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append("AND c.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate.trim() + " 00:00:00"));
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append("AND c.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate.trim() + " 23:59:59"));
        }
    }

    @Override
    public Map<String, Integer> countByStatus() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("Submitted", 0);
        map.put("Under Review", 0);
        map.put("In Progress", 0);
        map.put("Resolved", 0);
        map.put("Rejected", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM complaints WHERE deleted_at IS NULL GROUP BY status";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            logger.error("Error counting complaints by status", e);
        }
        return map;
    }

    @Override
    public Map<String, Integer> countByDepartment() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT d.name, COUNT(c.id) AS cnt FROM departments d " +
                     "LEFT JOIN complaints c ON d.id = c.department_id AND c.deleted_at IS NULL " +
                     "GROUP BY d.id, d.name ORDER BY d.name";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("name"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            logger.error("Error counting complaints by department", e);
        }
        return map;
    }

    @Override
    public Map<String, Integer> countByCategory() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT category, COUNT(*) AS cnt FROM complaints WHERE deleted_at IS NULL GROUP BY category ORDER BY cnt DESC";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("category"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            logger.error("Error counting complaints by category", e);
        }
        return map;
    }

    @Override
    public Map<String, Integer> countByPriority() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("High", 0);
        map.put("Medium", 0);
        map.put("Low", 0);
        String sql = "SELECT priority, COUNT(*) AS cnt FROM complaints WHERE deleted_at IS NULL GROUP BY priority";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("priority"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            logger.error("Error counting complaints by priority", e);
        }
        return map;
    }

    @Override
    public Map<String, Integer> getTrendData(int days) {
        Map<String, Integer> trend = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            trend.put(now.minusDays(i).toString(), 0);
        }

        String sql = "SELECT CAST(created_at AS DATE) AS dt, COUNT(*) AS cnt FROM complaints " +
                "WHERE deleted_at IS NULL AND created_at >= ? " +
                "GROUP BY CAST(created_at AS DATE) ORDER BY dt ASC";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp cutoff = Timestamp.valueOf(now.minusDays(days).atStartOfDay());
            ps.setTimestamp(1, cutoff);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date dt = rs.getDate("dt");
                    if (dt != null) {
                        trend.put(dt.toString(), rs.getInt("cnt"));
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting complaint trend data", e);
        }
        return trend;
    }

    @Override
    public double getAverageResolutionHours() {
        String sql = "SELECT created_at, updated_at FROM complaints WHERE status = 'Resolved' AND deleted_at IS NULL";
        long totalHours = 0;
        int count = 0;
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp created = rs.getTimestamp("created_at");
                Timestamp updated = rs.getTimestamp("updated_at");
                if (created != null && updated != null) {
                    long diff = ChronoUnit.HOURS.between(created.toInstant(), updated.toInstant());
                    if (diff < 0) diff = 1;
                    totalHours += Math.max(diff, 1);
                    count++;
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating average resolution hours", e);
        }
        return count == 0 ? 0.0 : Math.round(((double) totalHours / count) * 10.0) / 10.0;
    }

    @Override
    public boolean updateStatus(int id, String newStatus, String changedBy, String remark) {
        String getOldStatusSql = "SELECT status FROM complaints WHERE id = ?";
        String updateSql = "UPDATE complaints SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DbPool.getConnection()) {
            conn.setAutoCommit(false);
            String oldStatus = null;
            try (PreparedStatement psGet = conn.prepareStatement(getOldStatusSql)) {
                psGet.setInt(1, id);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) {
                        oldStatus = rs.getString("status");
                    }
                }
            }

            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setString(1, newStatus);
                psUpdate.setInt(2, id);
                int updated = psUpdate.executeUpdate();
                if (updated > 0) {
                    insertStatusLog(conn, id, oldStatus, newStatus, changedBy, remark);
                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            logger.error("Error updating status for complaint id: " + id, e);
            throw new RuntimeException("Database error updating status", e);
        }
        return false;
    }

    @Override
    public boolean updateInternalNotes(int id, String notes) {
        String sql = "UPDATE complaints SET internal_notes = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notes);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating internal notes for id: " + id, e);
            throw new RuntimeException("Database error updating internal notes", e);
        }
    }

    @Override
    public boolean updatePublicRemark(int id, String remark) {
        String sql = "UPDATE complaints SET public_remark = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, remark);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating public remark for id: " + id, e);
            throw new RuntimeException("Database error updating public remark", e);
        }
    }

    @Override
    public boolean assignStaff(int id, String staffName) {
        String sql = "UPDATE complaints SET assigned_to = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffName);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error assigning staff for complaint id: " + id, e);
            throw new RuntimeException("Database error assigning staff", e);
        }
    }

    @Override
    public boolean updateAttachmentPath(int id, String attachmentPath) {
        String sql = "UPDATE complaints SET attachment_path = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, attachmentPath);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating attachment path for complaint id: " + id, e);
            throw new RuntimeException("Database error updating attachment path", e);
        }
    }

    @Override
    public boolean setPublic(int id, boolean isPublic) {
        String sql = "UPDATE complaints SET is_public = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isPublic);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error setting public flag for complaint id: " + id, e);
            throw new RuntimeException("Database error setting public flag", e);
        }
    }

    @Override
    public boolean softDelete(int id) {
        String sql = "UPDATE complaints SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error soft-deleting complaint id: " + id, e);
            throw new RuntimeException("Database error soft-deleting complaint", e);
        }
    }

    @Override
    public List<Complaint> findPublicResolved(int limit) {
        List<Complaint> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE c.status = 'Resolved' AND c.is_public = TRUE AND c.deleted_at IS NULL ORDER BY c.updated_at DESC LIMIT ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching public resolved complaints", e);
        }
        return list;
    }

    @Override
    public List<Complaint> findRecent(int limit) {
        List<Complaint> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE c.deleted_at IS NULL ORDER BY c.created_at DESC LIMIT ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching recent complaints", e);
        }
        return list;
    }

    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM complaints WHERE deleted_at IS NULL";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error getting total complaint count", e);
        }
        return 0;
    }

    @Override
    public int getResolvedCount() {
        String sql = "SELECT COUNT(*) FROM complaints WHERE status = 'Resolved' AND deleted_at IS NULL";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Error getting resolved complaint count", e);
        }
        return 0;
    }

    private void insertStatusLog(Connection conn, int complaintId, String oldStatus, String newStatus, String changedBy, String remark) throws SQLException {
        String sql = "INSERT INTO status_logs (complaint_id, old_status, new_status, changed_by, remark) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            ps.setString(2, oldStatus);
            ps.setString(3, newStatus);
            ps.setString(4, changedBy != null ? changedBy : "System");
            ps.setString(5, remark);
            ps.executeUpdate();
        }
    }

    private Complaint mapResultSetToComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setId(rs.getInt("id"));
        c.setTrackingId(rs.getString("tracking_id"));
        int sId = rs.getInt("student_id");
        c.setStudentId(rs.wasNull() ? null : sId);
        c.setStudentName(rs.getString("student_name"));
        c.setRollNumber(rs.getString("roll_number"));
        c.setEmail(rs.getString("email"));
        c.setDepartmentId(rs.getInt("department_id"));
        c.setDepartmentName(rs.getString("department_name"));
        c.setDepartmentCode(rs.getString("department_code"));
        c.setCategory(rs.getString("category"));
        c.setPriority(rs.getString("priority"));
        c.setSubject(rs.getString("subject"));
        c.setDescription(rs.getString("description"));
        c.setAnonymous(rs.getBoolean("is_anonymous"));
        c.setStatus(rs.getString("status"));
        c.setAttachmentPath(rs.getString("attachment_path"));
        c.setInternalNotes(rs.getString("internal_notes"));
        c.setPublicRemark(rs.getString("public_remark"));
        c.setAssignedTo(rs.getString("assigned_to"));
        c.setPublic(rs.getBoolean("is_public"));
        c.setDeletedAt(rs.getTimestamp("deleted_at"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }

    @Override
    public List<Complaint> findByStudentId(int studentId, String status, String search, int offset, int limit) {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE c.student_id = ? AND c.deleted_at IS NULL ");

        List<Object> params = new ArrayList<>();
        params.add(studentId);

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) {
            sql.append("AND c.status = ? ");
            params.add(status.trim());
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(c.subject) LIKE ? OR LOWER(c.tracking_id) LIKE ? OR LOWER(c.category) LIKE ?) ");
            String term = "%" + search.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        sql.append("ORDER BY c.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit > 0 ? limit : 20);
        params.add(Math.max(0, offset));

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComplaint(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding complaints for student: " + studentId, e);
        }
        return list;
    }

    @Override
    public int countByStudentId(int studentId, String status, String search) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM complaints c WHERE c.student_id = ? AND c.deleted_at IS NULL ");
        List<Object> params = new ArrayList<>();
        params.add(studentId);

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) {
            sql.append("AND c.status = ? ");
            params.add(status.trim());
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(c.subject) LIKE ? OR LOWER(c.tracking_id) LIKE ? OR LOWER(c.category) LIKE ?) ");
            String term = "%" + search.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting complaints for student: " + studentId, e);
        }
        return 0;
    }

    @Override
    public Complaint findByIdAndStudentId(int id, int studentId) {
        String sql = BASE_SELECT + "WHERE c.id = ? AND c.student_id = ? AND c.deleted_at IS NULL";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComplaint(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding complaint " + id + " for student " + studentId, e);
        }
        return null;
    }

    @Override
    public Complaint findByTrackingIdAndStudentId(String trackingId, int studentId) {
        String sql = BASE_SELECT + "WHERE c.tracking_id = ? AND c.student_id = ? AND c.deleted_at IS NULL";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToComplaint(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding complaint by trackingId " + trackingId + " for student " + studentId, e);
        }
        return null;
    }

    @Override
    public Map<String, Integer> countByStatusForStudent(int studentId) {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("Submitted", 0);
        map.put("Under Review", 0);
        map.put("In Progress", 0);
        map.put("Resolved", 0);
        map.put("Rejected", 0);

        String sql = "SELECT status, COUNT(*) FROM complaints WHERE student_id = ? AND deleted_at IS NULL GROUP BY status";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting status for student " + studentId, e);
        }
        return map;
    }
}
