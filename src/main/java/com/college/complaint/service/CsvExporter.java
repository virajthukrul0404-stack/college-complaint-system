package com.college.complaint.service;

import com.college.complaint.model.Complaint;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvExporter {

    public static void exportComplaintsToCsv(List<Complaint> complaints, Writer writer) throws IOException {
        // UTF-8 BOM for Excel compatibility
        writer.write('\ufeff');

        // Header
        writer.write("Tracking ID,Student Name,Roll Number,Email,Department,Category,Priority,Status,Subject,Description,Assigned To,Public Remark,Internal Notes,Filed At,Updated At\n");

        for (Complaint c : complaints) {
            StringBuilder sb = new StringBuilder();
            sb.append(escapeCsv(c.getTrackingId())).append(",");
            sb.append(escapeCsv(c.isAnonymous() ? "Anonymous Student" : c.getStudentName())).append(",");
            sb.append(escapeCsv(c.isAnonymous() ? "Hidden" : c.getRollNumber())).append(",");
            sb.append(escapeCsv(c.isAnonymous() ? "Hidden" : c.getEmail())).append(",");
            sb.append(escapeCsv(c.getDepartmentName())).append(",");
            sb.append(escapeCsv(c.getCategory())).append(",");
            sb.append(escapeCsv(c.getPriority())).append(",");
            sb.append(escapeCsv(c.getStatus())).append(",");
            sb.append(escapeCsv(c.getSubject())).append(",");
            sb.append(escapeCsv(c.getDescription())).append(",");
            sb.append(escapeCsv(c.getAssignedTo() != null ? c.getAssignedTo() : "")).append(",");
            sb.append(escapeCsv(c.getPublicRemark() != null ? c.getPublicRemark() : "")).append(",");
            sb.append(escapeCsv(c.getInternalNotes() != null ? c.getInternalNotes() : "")).append(",");
            sb.append(escapeCsv(c.getCreatedAt() != null ? c.getCreatedAt().toString() : "")).append(",");
            sb.append(escapeCsv(c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : "")).append("\n");
            writer.write(sb.toString());
        }
        writer.flush();
    }

    private static String escapeCsv(String value) {
        if (value == null) return "\"\"";
        String val = value;
        if (val.startsWith("=") || val.startsWith("+") || val.startsWith("-") || val.startsWith("@")) {
            val = "'" + val;
        }
        String escaped = val.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
