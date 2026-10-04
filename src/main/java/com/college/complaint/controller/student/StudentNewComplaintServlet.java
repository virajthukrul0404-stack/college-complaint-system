package com.college.complaint.controller.student;

import com.college.complaint.dao.AttachmentDAO;
import com.college.complaint.dao.impl.AttachmentDAOImpl;
import com.college.complaint.dao.ComplaintDAO;
import com.college.complaint.dao.DepartmentDAO;
import com.college.complaint.dao.impl.ComplaintDAOImpl;
import com.college.complaint.dao.impl.DepartmentDAOImpl;
import com.college.complaint.model.Complaint;
import com.college.complaint.model.Department;
import com.college.complaint.model.Student;
import com.college.complaint.service.EventBroadcaster;
import com.college.complaint.service.TrackingIdService;
import com.college.complaint.service.ValidationService;
import com.college.complaint.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "StudentNewComplaintServlet", urlPatterns = "/student/complaint/new")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1 MB threshold in memory
        maxFileSize = 2 * 1024 * 1024,        // 2 MB max file size
        maxRequestSize = 6 * 1024 * 1024      // 6 MB max request size
)
public class StudentNewComplaintServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StudentNewComplaintServlet.class);
    private final ComplaintDAO complaintDAO = new ComplaintDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private final AttachmentDAO attachmentDAO = new AttachmentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        req.setAttribute("student", student);
        req.setAttribute("departments", departmentDAO.findAll());
        req.setAttribute("categories", ValidationService.ALLOWED_CATEGORIES);
        req.setAttribute("priorities", ValidationService.ALLOWED_PRIORITIES);

        req.getRequestDispatcher("/WEB-INF/views/student/complaint_new.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Student student = (Student) session.getAttribute("studentUser");

        String category = req.getParameter("category");
        String priority = req.getParameter("priority");
        String subject = req.getParameter("subject");
        String description = req.getParameter("description");
        boolean isAnonymous = "true".equalsIgnoreCase(req.getParameter("isAnonymous")) ||
                              "on".equalsIgnoreCase(req.getParameter("isAnonymous")) ||
                              "true".equalsIgnoreCase(req.getParameter("anonymous")) ||
                              "on".equalsIgnoreCase(req.getParameter("anonymous"));
        boolean isPublic = "true".equalsIgnoreCase(req.getParameter("isPublic")) ||
                           "on".equalsIgnoreCase(req.getParameter("isPublic")) ||
                           "true".equalsIgnoreCase(req.getParameter("public")) ||
                           "on".equalsIgnoreCase(req.getParameter("public"));

        Complaint complaint = new Complaint();
        complaint.setStudentId(student.getId());
        complaint.setStudentName(student.getFullName());
        complaint.setRollNumber(student.getRollNo());
        complaint.setEmail(student.getEmail());
        complaint.setDepartmentId(student.getDepartmentId());
        complaint.setCategory(category != null ? category.trim() : "");
        complaint.setPriority(priority != null ? priority.trim() : "Medium");
        complaint.setSubject(subject != null ? subject.trim() : "");
        complaint.setDescription(description != null ? description.trim() : "");
        complaint.setAnonymous(isAnonymous);
        complaint.setPublic(isPublic);

        Map<String, String> errors = ValidationService.validateComplaint(complaint);

        // Attachment handling
        Part filePart = null;
        String contentType = req.getContentType();
        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            try {
                filePart = req.getPart("attachment");
            } catch (Exception e) {
                logger.warn("Error reading multipart attachment: {}", e.getMessage());
            }
        }

        if (filePart != null && filePart.getSize() > 0) {
            String fileErr = FileUploadUtil.validateFile(filePart);
            if (fileErr != null) {
                errors.put("attachment", fileErr);
            }
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("complaint", complaint);
            req.setAttribute("student", student);
            req.setAttribute("departments", departmentDAO.findAll());
            req.setAttribute("categories", ValidationService.ALLOWED_CATEGORIES);
            req.setAttribute("priorities", ValidationService.ALLOWED_PRIORITIES);
            req.getRequestDispatcher("/WEB-INF/views/student/complaint_new.jsp").forward(req, resp);
            return;
        }

        byte[] fileBytes = null;
        String diskAttachmentPath = null;

        // Process attachment if present
        if (filePart != null && filePart.getSize() > 0) {
            try {
                if (com.college.complaint.util.AppConfig.isDbStorage()) {
                    fileBytes = FileUploadUtil.readFileBytes(filePart);
                } else {
                    String uploadDir = req.getServletContext().getRealPath("/uploads");
                    diskAttachmentPath = FileUploadUtil.saveFile(filePart, uploadDir);
                    complaint.setAttachmentPath(diskAttachmentPath);
                }
            } catch (Exception e) {
                logger.error("Failed to process uploaded attachment", e);
                errors.put("attachment", "File upload failed: " + e.getMessage());
                req.setAttribute("errors", errors);
                req.setAttribute("complaint", complaint);
                req.setAttribute("student", student);
                req.setAttribute("departments", departmentDAO.findAll());
                req.setAttribute("categories", ValidationService.ALLOWED_CATEGORIES);
                req.setAttribute("priorities", ValidationService.ALLOWED_PRIORITIES);
                req.getRequestDispatcher("/WEB-INF/views/student/complaint_new.jsp").forward(req, resp);
                return;
            }
        }

        // Generate tracking ID
        String trackingId = TrackingIdService.generateTrackingId();
        complaint.setTrackingId(trackingId);
        complaint.setStatus("Submitted");

        boolean created = complaintDAO.create(complaint);
        if (!created) {
            req.setAttribute("systemError", "Database error occurred while filing complaint. Please retry.");
            req.setAttribute("complaint", complaint);
            req.setAttribute("student", student);
            req.setAttribute("departments", departmentDAO.findAll());
            req.setAttribute("categories", ValidationService.ALLOWED_CATEGORIES);
            req.setAttribute("priorities", ValidationService.ALLOWED_PRIORITIES);
            req.getRequestDispatcher("/WEB-INF/views/student/complaint_new.jsp").forward(req, resp);
            return;
        }

        // If storing in DB, save attachment blob now that we have complaint.getId()
        if (fileBytes != null && fileBytes.length > 0) {
            try {
                String sha256 = FileUploadUtil.computeSha256(fileBytes);
                com.college.complaint.model.Attachment att = new com.college.complaint.model.Attachment(
                        complaint.getId(),
                        filePart.getSubmittedFileName(),
                        filePart.getContentType(),
                        fileBytes.length,
                        sha256,
                        fileBytes
                );
                Long attId = attachmentDAO.save(att);
                String attPath = "complaint/attachment?id=" + attId;
                complaint.setAttachmentPath(attPath);
                complaintDAO.updateAttachmentPath(complaint.getId(), attPath);
                logger.info("Saved complaint attachment {} ({} bytes, sha256={}) to database",
                        filePart.getSubmittedFileName(), fileBytes.length, sha256);
            } catch (Exception e) {
                logger.error("Failed to save attachment to database for complaint {}", complaint.getId(), e);
            }
        }

        // Broadcast to admin dashboard
        Department dept = departmentDAO.findById(complaint.getDepartmentId());
        String deptName = (dept != null) ? dept.getName() : "General";
        EventBroadcaster.broadcastNewComplaint(
                trackingId,
                complaint.getSubject(),
                deptName,
                complaint.getPriority(),
                complaint.getCategory()
        );

        logger.info("Student {} filed complaint {} (Anonymous: {})",
                student.getRollNo(), trackingId, complaint.isAnonymous());

        // Redirect to receipt view
        resp.sendRedirect(req.getContextPath() + "/complaint/receipt?id=" + trackingId + "&submitted=true");
    }
}
