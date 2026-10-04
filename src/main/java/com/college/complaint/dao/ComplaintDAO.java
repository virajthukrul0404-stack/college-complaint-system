package com.college.complaint.dao;

import com.college.complaint.model.Complaint;

import java.util.List;
import java.util.Map;

public interface ComplaintDAO {
    boolean create(Complaint complaint);
    Complaint findById(int id);
    Complaint findByTrackingId(String trackingId);
    Complaint findByTrackingIdAndEmail(String trackingId, String email);

    List<Complaint> searchAndFilter(
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
    );

    int countSearchAndFilter(
            String search,
            String status,
            Integer departmentId,
            String category,
            String priority,
            String startDate,
            String endDate
    );

    Map<String, Integer> countByStatus();
    Map<String, Integer> countByDepartment();
    Map<String, Integer> countByCategory();
    Map<String, Integer> countByPriority();
    Map<String, Integer> getTrendData(int days);
    double getAverageResolutionHours();

    boolean updateStatus(int id, String newStatus, String changedBy, String remark);
    boolean updateInternalNotes(int id, String notes);
    boolean updatePublicRemark(int id, String remark);
    boolean assignStaff(int id, String staffName);
    boolean updateAttachmentPath(int id, String attachmentPath);
    boolean setPublic(int id, boolean isPublic);
    boolean softDelete(int id);

    List<Complaint> findPublicResolved(int limit);
    List<Complaint> findRecent(int limit);
    int getTotalCount();
    int getResolvedCount();

    // Student ownership queries
    List<Complaint> findByStudentId(int studentId, String status, String search, int offset, int limit);
    int countByStudentId(int studentId, String status, String search);
    Complaint findByIdAndStudentId(int id, int studentId);
    Complaint findByTrackingIdAndStudentId(String trackingId, int studentId);
    Map<String, Integer> countByStatusForStudent(int studentId);
}
