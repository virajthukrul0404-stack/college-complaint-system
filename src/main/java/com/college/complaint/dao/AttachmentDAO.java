package com.college.complaint.dao;

import com.college.complaint.model.Attachment;

public interface AttachmentDAO {
    Long save(Attachment attachment);
    Attachment findById(Long id);
    Attachment findByComplaintId(int complaintId);
    boolean deleteByComplaintId(int complaintId);
}
