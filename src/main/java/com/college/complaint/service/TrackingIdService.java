package com.college.complaint.service;

import com.college.complaint.util.DbPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

public class TrackingIdService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingIdService.class);
    private static final AtomicInteger counter = new AtomicInteger(-1);

    private static void initCounterIfNeeded() {
        if (counter.get() < 0) {
            synchronized (counter) {
                if (counter.get() < 0) {
                    int maxFound = 0;
                    try (Connection conn = DbPool.getConnection()) {
                        try (PreparedStatement ps = conn.prepareStatement("SELECT COALESCE(MAX(id), 0) FROM complaints");
                             ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                maxFound = Math.max(maxFound, rs.getInt(1));
                            }
                        }
                        try (PreparedStatement ps = conn.prepareStatement("SELECT tracking_id FROM complaints WHERE tracking_id LIKE 'CMP-%'");
                             ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                String tId = rs.getString(1);
                                if (tId != null) {
                                    int lastDash = tId.lastIndexOf('-');
                                    if (lastDash >= 0 && lastDash < tId.length() - 1) {
                                        try {
                                            int num = Integer.parseInt(tId.substring(lastDash + 1));
                                            if (num > maxFound) {
                                                maxFound = num;
                                            }
                                        } catch (NumberFormatException ignored) {}
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Could not query max complaint tracking counter: " + e.getMessage());
                    }
                    counter.set(maxFound);
                }
            }
        }
    }

    public static String generateTrackingId() {
        initCounterIfNeeded();
        int currentYear = Year.now().getValue();
        while (true) {
            int nextVal = counter.incrementAndGet();
            String candidate = String.format("CMP-%d-%05d", currentYear, nextVal);
            if (!exists(candidate)) {
                return candidate;
            }
        }
    }

    private static boolean exists(String trackingId) {
        String sql = "SELECT 1 FROM complaints WHERE tracking_id = ?";
        try (Connection conn = DbPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
