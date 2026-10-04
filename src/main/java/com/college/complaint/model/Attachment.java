package com.college.complaint.model;

import java.sql.Timestamp;

public class Attachment {
    private Long id;
    private int complaintId;
    private String fileName;
    private String fileType;
    private long fileSize;
    private String sha256;
    private byte[] data;
    private Timestamp createdAt;

    public Attachment() {}

    public Attachment(int complaintId, String fileName, String fileType, long fileSize, String sha256, byte[] data) {
        this.complaintId = complaintId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.sha256 = sha256;
        this.data = data;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getSha256() { return sha256; }
    public void setSha256(String sha256) { this.sha256 = sha256; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
