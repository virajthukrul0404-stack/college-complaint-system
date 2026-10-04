package com.college.complaint.service;

import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS = 5 * 60 * 1000; // 5 minutes in milliseconds
    private static final long WINDOW_DURATION_MS = 5 * 60 * 1000; // 5 minutes sliding window

    private static class AttemptRecord {
        final LinkedList<Long> timestamps = new LinkedList<>();
        long lockedUntil = 0;
    }

    private static final Map<String, AttemptRecord> records = new ConcurrentHashMap<>();

    private static String buildKey(String ip, String username) {
        String safeIp = (ip != null) ? ip.trim() : "unknown";
        String safeUser = (username != null) ? username.trim().toLowerCase() : "unknown";
        return safeIp + ":" + safeUser;
    }

    public static synchronized boolean isLocked(String ip, String username) {
        String key = buildKey(ip, username);
        AttemptRecord record = records.get(key);
        if (record == null) {
            return false;
        }

        long now = System.currentTimeMillis();
        if (now < record.lockedUntil) {
            return true;
        }

        // Clean expired timestamps
        cleanExpired(record, now);
        return false;
    }

    public static synchronized long getRemainingLockSeconds(String ip, String username) {
        String key = buildKey(ip, username);
        AttemptRecord record = records.get(key);
        if (record == null) return 0;

        long now = System.currentTimeMillis();
        if (now < record.lockedUntil) {
            return (record.lockedUntil - now) / 1000;
        }
        return 0;
    }

    public static synchronized int getFailedAttempts(String ip, String username) {
        String key = buildKey(ip, username);
        AttemptRecord record = records.get(key);
        if (record == null) return 0;
        long now = System.currentTimeMillis();
        cleanExpired(record, now);
        return record.timestamps.size();
    }

    public static synchronized void recordFailedAttempt(String ip, String username) {
        String key = buildKey(ip, username);
        AttemptRecord record = records.computeIfAbsent(key, k -> new AttemptRecord());

        long now = System.currentTimeMillis();
        cleanExpired(record, now);

        record.timestamps.add(now);
        if (record.timestamps.size() >= MAX_ATTEMPTS) {
            record.lockedUntil = now + LOCK_DURATION_MS;
            record.timestamps.clear();
        }
    }

    public static synchronized void recordSuccess(String ip, String username) {
        String key = buildKey(ip, username);
        records.remove(key);
    }

    private static void cleanExpired(AttemptRecord record, long now) {
        Iterator<Long> it = record.timestamps.iterator();
        while (it.hasNext()) {
            if (now - it.next() > WINDOW_DURATION_MS) {
                it.remove();
            } else {
                break;
            }
        }
    }

    public static synchronized void resetAll() {
        records.clear();
    }
}
