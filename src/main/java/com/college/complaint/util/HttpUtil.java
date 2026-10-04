package com.college.complaint.util;

import jakarta.servlet.http.HttpServletRequest;

public class HttpUtil {

    public static String getClientIp(HttpServletRequest req) {
        if (req == null) return "unknown";

        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.trim().isEmpty()) {
            String[] parts = xff.split(",");
            String client = parts[0].trim();
            if (!client.isEmpty() && !"unknown".equalsIgnoreCase(client)) {
                return client;
            }
        }

        String xRealIp = req.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.trim().isEmpty() && !"unknown".equalsIgnoreCase(xRealIp.trim())) {
            return xRealIp.trim();
        }

        return req.getRemoteAddr() != null ? req.getRemoteAddr() : "unknown";
    }

    public static boolean isSecureRequest(HttpServletRequest req) {
        if (req == null) return false;
        if (req.isSecure()) return true;
        String proto = req.getHeader("X-Forwarded-Proto");
        return "https".equalsIgnoreCase(proto);
    }
}
