package com.college.complaint.util;

import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileUploadUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".webp");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");

    public static String validateFile(Part part) {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        if (part.getSize() > MAX_FILE_SIZE) {
            return "Attachment size exceeds maximum limit of 2 MB.";
        }
        String contentType = part.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return "Invalid file type. Only JPG, PNG, and WEBP images are allowed.";
        }
        String submittedFilename = part.getSubmittedFileName();
        if (submittedFilename == null || submittedFilename.trim().isEmpty()) {
            return null;
        }
        String extension = "";
        int dotIndex = submittedFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = submittedFilename.substring(dotIndex).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return "Invalid file extension. Only .jpg, .jpeg, .png, and .webp are allowed.";
        }

        // Magic bytes validation
        try (InputStream is = part.getInputStream()) {
            byte[] header = new byte[12];
            int read = is.read(header);
            if (read < 4 || !matchesMagicBytes(header, contentType.toLowerCase())) {
                return "File content does not match genuine image signature.";
            }
        } catch (IOException e) {
            logger.warn("Could not read magic bytes from upload: {}", e.getMessage());
            return "Unable to verify image contents.";
        }

        return null;
    }

    private static boolean matchesMagicBytes(byte[] header, String mimeType) {
        if (header == null || header.length < 4) return false;

        // JPEG: FF D8 FF
        if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
            return (header[0] & 0xFF) == 0xFF &&
                   (header[1] & 0xFF) == 0xD8 &&
                   (header[2] & 0xFF) == 0xFF;
        }

        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if (mimeType.contains("png")) {
            return header.length >= 8 &&
                   (header[0] & 0xFF) == 0x89 &&
                   (header[1] & 0xFF) == 0x50 &&
                   (header[2] & 0xFF) == 0x4E &&
                   (header[3] & 0xFF) == 0x47;
        }

        // WEBP: 'RIFF' .... 'WEBP'
        if (mimeType.contains("webp")) {
            if (header.length < 12) return false;
            boolean isRiff = header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F';
            boolean isWebp = header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
            return isRiff && isWebp;
        }

        return false;
    }

    public static byte[] readFileBytes(Part part) throws IOException {
        try (InputStream input = part.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            long total = 0;
            while ((bytesRead = input.read(buffer)) != -1) {
                total += bytesRead;
                if (total > MAX_FILE_SIZE) {
                    throw new IllegalArgumentException("Attachment size exceeds maximum limit of 2 MB.");
                }
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }

    public static String computeSha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static String saveFile(Part part, String baseUploadDir) throws IOException {
        return saveUploadedFile(part, baseUploadDir);
    }

    public static String saveUploadedFile(Part part, String baseUploadDir) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }

        if (part.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Attachment size exceeds maximum limit of 2 MB.");
        }

        String contentType = part.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, and WEBP images are allowed.");
        }

        String submittedFilename = part.getSubmittedFileName();
        if (submittedFilename == null || submittedFilename.trim().isEmpty()) {
            return null;
        }

        String extension = "";
        int dotIndex = submittedFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = submittedFilename.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Invalid file extension. Only .jpg, .jpeg, .png, and .webp are allowed.");
        }

        File uploadDir = new File(baseUploadDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String safeFilename = UUID.randomUUID().toString() + extension;
        File destinationFile = new File(uploadDir, safeFilename);

        try (InputStream input = part.getInputStream()) {
            Files.copy(input, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        logger.info("Saved uploaded file {} as {}", submittedFilename, safeFilename);
        return "uploads/" + safeFilename;
    }
}
