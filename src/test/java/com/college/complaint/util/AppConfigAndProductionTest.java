package com.college.complaint.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import static org.junit.jupiter.api.Assertions.*;

public class AppConfigAndProductionTest {

    @BeforeEach
    void setUp() {
        AppConfig.resetValidation();
    }

    @Test
    void testAppConfigDefaults() {
        assertNotNull(AppConfig.getAppEnv());
        assertTrue(AppConfig.getPort() > 0);
        assertNotNull(AppConfig.getDbDriver());
        assertNotNull(AppConfig.getDbUrl());
        assertNotNull(AppConfig.getDbUser());
        assertTrue(AppConfig.getDbPoolSize() >= 2);
        assertNotNull(AppConfig.getAdminInitialPassword());
        assertNotNull(AppConfig.getStorageMode());
    }

    @Test
    void testClientIpResolution() {
        // Test with X-Forwarded-For single IP
        MockRequest req1 = new MockRequest("10.0.0.1", "203.0.113.195", "https");
        assertEquals("203.0.113.195", HttpUtil.getClientIp(req1));
        assertTrue(HttpUtil.isSecureRequest(req1));

        // Test with X-Forwarded-For multi-hop chain
        MockRequest req2 = new MockRequest("10.0.0.1", "198.51.100.42, 10.0.0.5", "http");
        assertEquals("198.51.100.42", HttpUtil.getClientIp(req2));
        assertFalse(HttpUtil.isSecureRequest(req2));

        // Test with no proxy headers (direct IP)
        MockRequest req3 = new MockRequest("192.168.1.100", null, null);
        assertEquals("192.168.1.100", HttpUtil.getClientIp(req3));
        assertFalse(HttpUtil.isSecureRequest(req3));
    }

    @Test
    void testMagicBytesAndSha256Validation() throws IOException {
        // Test genuine PNG magic bytes
        byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D};
        MockPart pngPart = new MockPart("test.png", "image/png", pngBytes);
        assertNull(FileUploadUtil.validateFile(pngPart), "Valid PNG should pass validation");

        // Test genuine JPEG magic bytes
        byte[] jpegBytes = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01};
        MockPart jpegPart = new MockPart("test.jpg", "image/jpeg", jpegBytes);
        assertNull(FileUploadUtil.validateFile(jpegPart), "Valid JPEG should pass validation");

        // Test spoofed extension with fake text content
        byte[] fakeBytes = "<?php echo 'malicious'; ?>".getBytes();
        MockPart fakePart = new MockPart("shell.png", "image/png", fakeBytes);
        assertNotNull(FileUploadUtil.validateFile(fakePart), "Spoofed image must be rejected by magic bytes check");

        // Test SHA-256 computation
        String sha = FileUploadUtil.computeSha256("hello-notice-board".getBytes());
        assertNotNull(sha);
        assertEquals(64, sha.length());
    }

    // Lightweight mock request for unit testing
    static class MockRequest extends EmptyMockHttpServletRequest {
        private final String remoteAddr;
        private final String xff;
        private final String proto;

        MockRequest(String remoteAddr, String xff, String proto) {
            this.remoteAddr = remoteAddr;
            this.xff = xff;
            this.proto = proto;
        }

        @Override
        public String getRemoteAddr() { return remoteAddr; }

        @Override
        public String getHeader(String name) {
            if ("X-Forwarded-For".equalsIgnoreCase(name)) return xff;
            if ("X-Forwarded-Proto".equalsIgnoreCase(name)) return proto;
            return null;
        }

        @Override
        public boolean isSecure() { return "https".equalsIgnoreCase(proto); }
    }

    // Lightweight mock part
    static class MockPart implements Part {
        private final String filename;
        private final String contentType;
        private final byte[] data;

        MockPart(String filename, String contentType, byte[] data) {
            this.filename = filename;
            this.contentType = contentType;
            this.data = data;
        }

        @Override public InputStream getInputStream() { return new ByteArrayInputStream(data); }
        @Override public String getContentType() { return contentType; }
        @Override public String getName() { return "file"; }
        @Override public String getSubmittedFileName() { return filename; }
        @Override public long getSize() { return data.length; }
        @Override public void write(String fileName) {}
        @Override public void delete() {}
        @Override public String getHeader(String name) { return null; }
        @Override public Collection<String> getHeaders(String name) { return null; }
        @Override public Collection<String> getHeaderNames() { return null; }
    }

    static class EmptyMockHttpServletRequest implements HttpServletRequest {
        public Object getAttribute(String name) { return null; }
        public java.util.Enumeration<String> getAttributeNames() { return null; }
        public String getCharacterEncoding() { return null; }
        public void setCharacterEncoding(String env) {}
        public int getContentLength() { return 0; }
        public long getContentLengthLong() { return 0; }
        public String getContentType() { return null; }
        public jakarta.servlet.ServletInputStream getInputStream() { return null; }
        public String getParameter(String name) { return null; }
        public java.util.Enumeration<String> getParameterNames() { return null; }
        public String[] getParameterValues(String name) { return null; }
        public java.util.Map<String, String[]> getParameterMap() { return null; }
        public String getProtocol() { return "HTTP/1.1"; }
        public String getScheme() { return "http"; }
        public String getServerName() { return "localhost"; }
        public int getServerPort() { return 8080; }
        public java.io.BufferedReader getReader() { return null; }
        public String getRemoteAddr() { return "127.0.0.1"; }
        public String getRemoteHost() { return "localhost"; }
        public void setAttribute(String name, Object o) {}
        public void removeAttribute(String name) {}
        public java.util.Locale getLocale() { return java.util.Locale.getDefault(); }
        public java.util.Enumeration<java.util.Locale> getLocales() { return null; }
        public boolean isSecure() { return false; }
        public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }
        public int getRemotePort() { return 0; }
        public String getLocalName() { return null; }
        public String getLocalAddr() { return null; }
        public int getLocalPort() { return 0; }
        public jakarta.servlet.ServletContext getServletContext() { return null; }
        public jakarta.servlet.AsyncContext startAsync() { return null; }
        public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest r, jakarta.servlet.ServletResponse s) { return null; }
        public boolean isAsyncStarted() { return false; }
        public boolean isAsyncSupported() { return false; }
        public jakarta.servlet.AsyncContext getAsyncContext() { return null; }
        public jakarta.servlet.DispatcherType getDispatcherType() { return null; }
        public String getRequestId() { return null; }
        public String getProtocolRequestId() { return null; }
        public jakarta.servlet.ServletConnection getServletConnection() { return null; }
        public String getAuthType() { return null; }
        public jakarta.servlet.http.Cookie[] getCookies() { return null; }
        public long getDateHeader(String name) { return 0; }
        public String getHeader(String name) { return null; }
        public java.util.Enumeration<String> getHeaders(String name) { return null; }
        public java.util.Enumeration<String> getHeaderNames() { return null; }
        public int getIntHeader(String name) { return 0; }
        public String getMethod() { return "GET"; }
        public String getPathInfo() { return null; }
        public String getPathTranslated() { return null; }
        public String getContextPath() { return ""; }
        public String getQueryString() { return null; }
        public String getRemoteUser() { return null; }
        public boolean isUserInRole(String role) { return false; }
        public java.security.Principal getUserPrincipal() { return null; }
        public String getRequestedSessionId() { return null; }
        public String getRequestURI() { return "/"; }
        public StringBuffer getRequestURL() { return new StringBuffer("http://localhost/"); }
        public String getServletPath() { return "/"; }
        public jakarta.servlet.http.HttpSession getSession(boolean create) { return null; }
        public jakarta.servlet.http.HttpSession getSession() { return null; }
        public String changeSessionId() { return null; }
        public boolean isRequestedSessionIdValid() { return false; }
        public boolean isRequestedSessionIdFromCookie() { return false; }
        public boolean isRequestedSessionIdFromURL() { return false; }
        public boolean authenticate(jakarta.servlet.http.HttpServletResponse response) { return false; }
        public void login(String username, String password) {}
        public void logout() {}
        public java.util.Collection<Part> getParts() { return null; }
        public Part getPart(String name) { return null; }
        public <T extends jakarta.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) { return null; }
    }
}
