package com.college.complaint.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.regex.Pattern;

@WebFilter(filterName = "DeviceDetectionFilter", urlPatterns = "/*", asyncSupported = true)
public class DeviceDetectionFilter implements Filter {

    private static final Pattern TABLET_PATTERN =
            Pattern.compile("iPad|Tablet|(Android(?!.*Mobile))", Pattern.CASE_INSENSITIVE);
    private static final Pattern MOBILE_PATTERN =
            Pattern.compile("Mobile|Android|iPhone|iPod|BlackBerry|IEMobile|Opera Mini|webOS", Pattern.CASE_INSENSITIVE);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String viewParam = req.getParameter("view");
        HttpSession session = req.getSession(false);

        if (viewParam != null && !viewParam.trim().isEmpty()) {
            String v = viewParam.trim().toLowerCase();
            if ("mobile".equals(v) || "desktop".equals(v) || "tablet".equals(v)) {
                if (session == null) {
                    session = req.getSession(true);
                }
                session.setAttribute("DEVICE_VIEW_OVERRIDE", v);
            } else if ("auto".equals(v) || "reset".equals(v)) {
                if (session != null) {
                    session.removeAttribute("DEVICE_VIEW_OVERRIDE");
                }
            }
        }

        String deviceClass = null;
        if (session != null) {
            deviceClass = (String) session.getAttribute("DEVICE_VIEW_OVERRIDE");
        }

        if (deviceClass == null) {
            String userAgent = req.getHeader("User-Agent");
            deviceClass = classifyUserAgent(userAgent);
        }

        req.setAttribute("deviceClass", deviceClass);
        req.setAttribute("isMobile", "mobile".equals(deviceClass));
        req.setAttribute("isDesktop", "desktop".equals(deviceClass));
        req.setAttribute("isTablet", "tablet".equals(deviceClass));

        chain.doFilter(request, response);
    }

    public static String classifyUserAgent(String userAgent) {
        if (userAgent == null || userAgent.trim().isEmpty()) {
            return "desktop";
        }
        if (TABLET_PATTERN.matcher(userAgent).find()) {
            return "tablet";
        }
        if (MOBILE_PATTERN.matcher(userAgent).find()) {
            return "mobile";
        }
        return "desktop";
    }

    @Override
    public void destroy() {}
}
