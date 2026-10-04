package com.college.complaint.controller;

import com.college.complaint.service.EventBroadcaster;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "EventStreamServlet", urlPatterns = "/events", asyncSupported = true)
public class EventStreamServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EventStreamServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String channel = req.getParameter("channel");
        if (channel == null || channel.trim().isEmpty()) {
            channel = "global";
        }
        channel = channel.trim();

        jakarta.servlet.http.HttpSession session = req.getSession(false);
        com.college.complaint.model.Student student = (session != null) ? (com.college.complaint.model.Student) session.getAttribute("studentUser") : null;
        com.college.complaint.model.Admin admin = (session != null) ? (com.college.complaint.model.Admin) session.getAttribute("adminUser") : null;
        if (admin == null && session != null) {
            admin = (com.college.complaint.model.Admin) session.getAttribute("admin");
        }

        // Security check: student channel requires matching student ownership or admin
        if (channel.startsWith("student-")) {
            String studentIdStr = channel.substring("student-".length());
            boolean authorized = false;
            if (admin != null) {
                authorized = true;
            } else if (student != null) {
                try {
                    int requestedId = Integer.parseInt(studentIdStr);
                    if (student.getId() == requestedId) {
                        authorized = true;
                    }
                } catch (NumberFormatException ignored) {}
            }

            if (!authorized) {
                logger.warn("Unauthorized SSE subscription attempt to channel '{}'", channel);
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to requested event channel.");
                return;
            }
        } else if ("admin-feed".equalsIgnoreCase(channel)) {
            if (admin == null) {
                logger.warn("Unauthorized SSE subscription attempt to admin-feed");
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to admin stream.");
                return;
            }
        }

        // Parse Last-Event-ID for reconnection resilience
        Long lastEventId = null;
        String headerLastId = req.getHeader("Last-Event-ID");
        if (headerLastId != null && !headerLastId.trim().isEmpty()) {
            try {
                lastEventId = Long.parseLong(headerLastId.trim());
            } catch (NumberFormatException ignored) {}
        }
        if (lastEventId == null) {
            String paramLastId = req.getParameter("lastEventId");
            if (paramLastId != null && !paramLastId.trim().isEmpty()) {
                try {
                    lastEventId = Long.parseLong(paramLastId.trim());
                } catch (NumberFormatException ignored) {}
            }
        }

        resp.setContentType("text/event-stream;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache, no-transform");
        resp.setHeader("Connection", "keep-alive");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("X-Accel-Buffering", "no");

        AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(0); // Managed via keepalive heartbeats

        boolean accepted = EventBroadcaster.subscribe(channel, asyncContext, lastEventId);
        if (!accepted) {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            resp.setHeader("Retry-After", "10");
            asyncContext.complete();
            logger.warn("SSE connection limit reached, sent 503 Retry-After for channel: {}", channel);
            return;
        }

        logger.debug("Registered SSE client on channel: {} (Last-Event-ID: {})", channel, lastEventId);
    }
}
