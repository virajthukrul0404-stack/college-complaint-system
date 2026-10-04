package com.college.complaint.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class StatusLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int complaintId;
    private String oldStatus;
    private String newStatus;
    private String changedBy;
    private String remark;
    private Timestamp createdAt;

    public StatusLog() {}

    public StatusLog(int id, int complaintId, String oldStatus, String newStatus, String changedBy, String remark, Timestamp createdAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.remark = remark;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
