package com.college.complaint.dao;

import com.college.complaint.dao.impl.AttachmentDAOImpl;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.model.Attachment;
import com.college.complaint.model.Complaint;
import com.college.complaint.util.DatabaseInitializer;
import com.college.complaint.util.FileUploadUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class AttachmentDAOTest {

    private static AttachmentDAO attachmentDAO;
    private static ComplaintDAO complaintDAO;

    @BeforeAll
    static void init() {
        DatabaseInitializer.initialize();
        attachmentDAO = new AttachmentDAOImpl();
        complaintDAO = new ComplaintDAOImpl();
    }

    @Test
    void testSaveAndRetrieveAttachment() {
        // 1. Create a parent complaint
        Complaint c = new Complaint();
        c.setTrackingId("CMP-TEST-ATT-" + System.currentTimeMillis());
        c.setStudentName("Test Student");
        c.setRollNumber("22CS999");
        c.setEmail("test999@campus.edu");
        c.setDepartmentId(1);
        c.setCategory("Hostel");
        c.setPriority("Low");
        c.setSubject("Water Tap Leak");
        c.setDescription("Faucet leaking in room 102");
        c.setAnonymous(false);
        c.setStatus("Submitted");
        c.setPublic(false);

        boolean created = complaintDAO.create(c);
        assertTrue(created, "Complaint should be created");
        assertTrue(c.getId() > 0, "Complaint should have generated ID");

        // 2. Create attachment blob
        byte[] rawImageBytes = "FAKED_PNG_BINARY_DATA_FOR_BLOB_TESTING_12345".getBytes(StandardCharsets.UTF_8);
        String sha256 = FileUploadUtil.computeSha256(rawImageBytes);

        Attachment att = new Attachment(
                c.getId(),
                "faucet_evidence.png",
                "image/png",
                rawImageBytes.length,
                sha256,
                rawImageBytes
        );

        Long savedId = attachmentDAO.save(att);
        assertNotNull(savedId, "Saved attachment should return non-null ID");
        assertTrue(savedId > 0);

        // 3. Retrieve by ID
        Attachment fetchedById = attachmentDAO.findById(savedId);
        assertNotNull(fetchedById);
        assertEquals(c.getId(), fetchedById.getComplaintId());
        assertEquals("faucet_evidence.png", fetchedById.getFileName());
        assertEquals("image/png", fetchedById.getFileType());
        assertEquals(rawImageBytes.length, fetchedById.getFileSize());
        assertEquals(sha256, fetchedById.getSha256());
        assertArrayEquals(rawImageBytes, fetchedById.getData(), "Binary blob data must match exactly");

        // 4. Retrieve by Complaint ID
        Attachment fetchedByComplaint = attachmentDAO.findByComplaintId(c.getId());
        assertNotNull(fetchedByComplaint);
        assertEquals(savedId, fetchedByComplaint.getId());
        assertArrayEquals(rawImageBytes, fetchedByComplaint.getData());

        // 5. Delete by Complaint ID
        boolean deleted = attachmentDAO.deleteByComplaintId(c.getId());
        assertTrue(deleted, "Should delete attachment");
        assertNull(attachmentDAO.findById(savedId));
    }
}
