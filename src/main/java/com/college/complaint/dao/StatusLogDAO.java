package com.college.complaint.dao;

import com.college.complaint.model.StatusLog;
import java.util.List;

public interface StatusLogDAO {
    boolean create(StatusLog log);
    List<StatusLog> findByComplaintId(int complaintId);
}
