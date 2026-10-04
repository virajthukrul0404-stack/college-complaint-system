package com.college.complaint.dao.impl;

import com.college.complaint.dao.StatusLogDAO;
import com.college.complaint.model.StatusLog;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StatusLogDAOImpl implements StatusLogDAO {

    private static final Logger logger = LoggerFactory.getLogger(StatusLogDAOImpl.class);

    @Override
    public boolean create(StatusLog log) {
        String sql = "INSERT INTO status_logs (complaint_id, old_status, new_status, changed_by, remark) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, log.getComplaintId());
            ps.setString(2, log.getOldStatus());
            ps.setString(3, log.getNewStatus());
            ps.setString(4, log.getChangedBy());
            ps.setString(5, log.getRemark());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        log.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating status log for complaint: " + log.getComplaintId(), e);
            throw new RuntimeException("Database error saving status log", e);
        }
        return false;
    }

    @Override
    public List<StatusLog> findByComplaintId(int complaintId) {
        List<StatusLog> list = new ArrayList<>();
        String sql = "SELECT id, complaint_id, old_status, new_status, changed_by, remark, created_at " +
                     "FROM status_logs WHERE complaint_id = ? ORDER BY created_at ASC, id ASC";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StatusLog log = new StatusLog();
                    log.setId(rs.getInt("id"));
                    log.setComplaintId(rs.getInt("complaint_id"));
                    log.setOldStatus(rs.getString("old_status"));
                    log.setNewStatus(rs.getString("new_status"));
                    log.setChangedBy(rs.getString("changed_by"));
                    log.setRemark(rs.getString("remark"));
                    log.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(log);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching status logs for complaint: " + complaintId, e);
            throw new RuntimeException("Database error fetching status logs", e);
        }
        return list;
    }
}
