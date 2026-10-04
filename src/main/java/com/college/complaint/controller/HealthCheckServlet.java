package com.college.complaint.controller;

import com.college.complaint.util.DbPool;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;

@WebServlet(name = "HealthCheckServlet", urlPatterns = {"/healthz", "/healthz/db"})
public class HealthCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        PrintWriter out = resp.getWriter();
        long uptimeSeconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;

        if ("/healthz/db".equalsIgnoreCase(path)) {
            // Deep health check: tests database connectivity
            try (Connection conn = DbPool.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {

                if (rs.next()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    out.write(String.format(
                            "{\"status\":\"UP\",\"database\":\"CONNECTED\",\"uptimeSeconds\":%d,\"timestamp\":\"%s\"}",
                            uptimeSeconds, Instant.now().toString()
                    ));
                    return;
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                out.write(String.format(
                        "{\"status\":\"DOWN\",\"database\":\"UNAVAILABLE\",\"error\":\"%s\",\"uptimeSeconds\":%d,\"timestamp\":\"%s\"}",
                        e.getMessage().replace("\"", "\\\""), uptimeSeconds, Instant.now().toString()
                    ));
                return;
            }
        }

        // Standard cheap healthcheck for Render (does not touch DB)
        resp.setStatus(HttpServletResponse.SC_OK);
        out.write(String.format(
                "{\"status\":\"UP\",\"service\":\"college-complaint-system\",\"uptimeSeconds\":%d,\"timestamp\":\"%s\"}",
                uptimeSeconds, Instant.now().toString()
        ));
    }
}
