package com.college.complaint.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "AdminDeviceGuardFilter", urlPatterns = "/admin/*", asyncSupported = true)
public class AdminDeviceGuardFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow logout to proceed cleanly even from mobile
        if ("/admin/logout".equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        String deviceClass = (String) req.getAttribute("deviceClass");
        if (deviceClass == null) {
            deviceClass = DeviceDetectionFilter.classifyUserAgent(req.getHeader("User-Agent"));
        }

        // If detected as mobile phone (under 1024px device class) and not overridden to desktop
        if ("mobile".equalsIgnoreCase(deviceClass)) {
            req.setAttribute("currentAdminPath", path);
            req.getRequestDispatcher("/WEB-INF/views/admin/laptop_only.jsp").forward(req, res);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
