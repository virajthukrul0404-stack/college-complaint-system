package com.college.complaint.dao.impl;

import com.college.complaint.dao.AttachmentDAO;
import com.college.complaint.model.Attachment;
import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class AttachmentDAOImpl implements AttachmentDAO {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentDAOImpl.class);

    @Override
    public Long save(Attachment attachment) {
        String sql = "INSERT INTO complaint_attachments (complaint_id, file_name, file_type, file_size, sha256, data) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, attachment.getComplaintId());
            pstmt.setString(2, attachment.getFileName());
            pstmt.setString(3, attachment.getFileType());
            pstmt.setLong(4, attachment.getFileSize());
            pstmt.setString(5, attachment.getSha256());
            pstmt.setBytes(6, attachment.getData());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        attachment.setId(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving attachment for complaintId={}", attachment.getComplaintId(), e);
        }
        return null;
    }

    @Override
    public Attachment findById(Long id) {
        String sql = "SELECT id, complaint_id, file_name, file_type, file_size, sha256, data, created_at " +
                "FROM complaint_attachments WHERE id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding attachment by id={}", id, e);
        }
        return null;
    }

    @Override
    public Attachment findByComplaintId(int complaintId) {
        String sql = "SELECT id, complaint_id, file_name, file_type, file_size, sha256, data, created_at " +
                "FROM complaint_attachments WHERE complaint_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, complaintId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding attachment by complaintId={}", complaintId, e);
        }
        return null;
    }

    @Override
    public boolean deleteByComplaintId(int complaintId) {
        String sql = "DELETE FROM complaint_attachments WHERE complaint_id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, complaintId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting attachments for complaintId={}", complaintId, e);
            return false;
        }
    }

    private Attachment mapRow(ResultSet rs) throws SQLException {
        Attachment att = new Attachment();
        att.setId(rs.getLong("id"));
        att.setComplaintId(rs.getInt("complaint_id"));
        att.setFileName(rs.getString("file_name"));
        att.setFileType(rs.getString("file_type"));
        att.setFileSize(rs.getLong("file_size"));
        att.setSha256(rs.getString("sha256"));
        att.setData(rs.getBytes("data"));
        att.setCreatedAt(rs.getTimestamp("created_at"));
        return att;
    }
}
