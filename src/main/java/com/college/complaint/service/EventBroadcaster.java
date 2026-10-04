package com.college.complaint.service;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EventBroadcaster {

    private static final Logger logger = LoggerFactory.getLogger(EventBroadcaster.class);
    private static final int MAX_SSE_CONNECTIONS = 50; // Bound connections for 512MB RAM instance

    private static final Map<String, Set<AsyncContext>> channelSubscribers = new ConcurrentHashMap<>();
    private static final AtomicInteger totalConnections = new AtomicInteger(0);
    private static final AtomicLong eventIdCounter = new AtomicLong(0);

    // Simple bounded history buffer for Last-Event-ID replay (up to 100 recent events)
    private static final int MAX_HISTORY = 100;
    private static final List<StoredEvent> eventHistory = Collections.synchronizedList(new LinkedList<>());

    public static class StoredEvent {
        public final long id;
        public final String channel;
        public final String event;
        public final String data;

        public StoredEvent(long id, String channel, String event, String data) {
            this.id = id;
            this.channel = channel;
            this.event = event;
            this.data = data;
        }
    }

    private static final ScheduledExecutorService heartbeatScheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "SSE-Heartbeat-Thread");
                t.setDaemon(true);
                return t;
            });

    static {
        // Send heartbeat comment every 15 seconds to maintain proxy keep-alive
        heartbeatScheduler.scheduleAtFixedRate(EventBroadcaster::sendHeartbeat, 15, 15, TimeUnit.SECONDS);
    }

    public static boolean subscribe(String channel, AsyncContext asyncContext, Long lastEventId) {
        if (totalConnections.get() >= MAX_SSE_CONNECTIONS) {
            logger.warn("SSE connection rejected: reached max connections ({})", MAX_SSE_CONNECTIONS);
            return false;
        }

        String safeChannel = (channel != null && !channel.trim().isEmpty()) ? channel.trim() : "global";
        Set<AsyncContext> subscribers = channelSubscribers.computeIfAbsent(
                safeChannel,
                k -> Collections.newSetFromMap(new ConcurrentHashMap<>())
        );

        subscribers.add(asyncContext);
        totalConnections.incrementAndGet();
        logger.debug("Client subscribed to SSE channel '{}'. Total connections: {}", safeChannel, totalConnections.get());

        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) {
                removeSubscriber(safeChannel, asyncContext);
            }

            @Override
            public void onTimeout(AsyncEvent event) {
                removeSubscriber(safeChannel, asyncContext);
                try {
                    asyncContext.complete();
                } catch (Exception ignored) {}
            }

            @Override
            public void onError(AsyncEvent event) {
                removeSubscriber(safeChannel, asyncContext);
                try {
                    asyncContext.complete();
                } catch (Exception ignored) {}
            }

            @Override
            public void onStartAsync(AsyncEvent event) {}
        });

        // Send initial connect acknowledgement
        sendToContext(asyncContext, "connected", "{\"status\":\"connected\",\"channel\":\"" + safeChannel + "\"}");

        // Replay missed events if client reconnected with Last-Event-ID
        if (lastEventId != null && lastEventId > 0) {
            replayMissedEvents(asyncContext, safeChannel, lastEventId);
        }

        return true;
    }

    private static void replayMissedEvents(AsyncContext ctx, String channel, long lastId) {
        synchronized (eventHistory) {
            for (StoredEvent se : eventHistory) {
                if (se.id > lastId && (se.channel.equals(channel) || "global".equals(se.channel))) {
                    sendToContext(ctx, se.event, se.data, se.id);
                }
            }
        }
    }

    private static void removeSubscriber(String channel, AsyncContext asyncContext) {
        Set<AsyncContext> subscribers = channelSubscribers.get(channel);
        if (subscribers != null) {
            if (subscribers.remove(asyncContext)) {
                totalConnections.decrementAndGet();
            }
            if (subscribers.isEmpty()) {
                channelSubscribers.remove(channel);
            }
        }
    }

    public static void broadcast(String channel, String event, String data) {
        long id = eventIdCounter.incrementAndGet();

        // Store event in bounded history
        synchronized (eventHistory) {
            if (eventHistory.size() >= MAX_HISTORY) {
                eventHistory.remove(0);
            }
            eventHistory.add(new StoredEvent(id, channel, event, data));
        }

        Set<AsyncContext> subscribers = channelSubscribers.get(channel);
        if (subscribers != null && !subscribers.isEmpty()) {
            logger.info("Broadcasting event '{}' (id={}) to {} clients on channel '{}'",
                    event, id, subscribers.size(), channel);
            for (AsyncContext context : subscribers) {
                sendToContext(context, event, data, id);
            }
        }
    }

    public static void broadcastStatusUpdate(String trackingId, String newStatus, String remark, String updatedAt) {
        broadcastStudentStatusUpdate(null, trackingId, newStatus, remark, updatedAt);
    }

    public static void broadcastStudentStatusUpdate(Integer studentId, String trackingId, String newStatus, String remark, String updatedAt) {
        String json = String.format(
                "{\"trackingId\":\"%s\",\"status\":\"%s\",\"remark\":\"%s\",\"updatedAt\":\"%s\"}",
                escapeJson(trackingId),
                escapeJson(newStatus),
                escapeJson(remark != null ? remark : ""),
                escapeJson(updatedAt != null ? updatedAt : "")
        );
        if (studentId != null && studentId > 0) {
            broadcast("student-" + studentId, "statusUpdate", json);
        }
        broadcast("complaint-" + trackingId, "statusUpdate", json);
        broadcast("admin-feed", "statusUpdate", json);
    }

    public static void broadcastStudentNotification(Integer studentId, String message, String createdAt) {
        if (studentId != null && studentId > 0) {
            String json = String.format(
                    "{\"message\":\"%s\",\"createdAt\":\"%s\"}",
                    escapeJson(message),
                    escapeJson(createdAt != null ? createdAt : "")
            );
            broadcast("student-" + studentId, "notification", json);
        }
    }

    public static void broadcastNewComplaint(String trackingId, String subject, String departmentName, String priority, String category) {
        String json = String.format(
                "{\"trackingId\":\"%s\",\"subject\":\"%s\",\"department\":\"%s\",\"priority\":\"%s\",\"category\":\"%s\"}",
                escapeJson(trackingId),
                escapeJson(subject),
                escapeJson(departmentName),
                escapeJson(priority),
                escapeJson(category)
        );
        broadcast("admin-feed", "newComplaint", json);
    }

    private static void sendHeartbeat() {
        for (Map.Entry<String, Set<AsyncContext>> entry : channelSubscribers.entrySet()) {
            for (AsyncContext ctx : entry.getValue()) {
                try {
                    PrintWriter writer = ctx.getResponse().getWriter();
                    writer.write(": keepalive\n\n");
                    writer.flush();
                } catch (Exception e) {
                    removeSubscriber(entry.getKey(), ctx);
                    try {
                        ctx.complete();
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    private static void sendToContext(AsyncContext ctx, String event, String data) {
        sendToContext(ctx, event, data, null);
    }

    private static void sendToContext(AsyncContext ctx, String event, String data, Long id) {
        try {
            PrintWriter writer = ctx.getResponse().getWriter();
            if (id != null) {
                writer.write("id: " + id + "\n");
            }
            if (event != null) {
                writer.write("event: " + event + "\n");
            }
            writer.write("data: " + data + "\n\n");
            writer.flush();
        } catch (IOException e) {
            try {
                ctx.complete();
            } catch (Exception ignored) {}
        }
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static int getActiveConnectionsCount() {
        return totalConnections.get();
    }

    public static void shutdown() {
        heartbeatScheduler.shutdownNow();
        for (Map.Entry<String, Set<AsyncContext>> entry : channelSubscribers.entrySet()) {
            for (AsyncContext ctx : entry.getValue()) {
                try {
                    PrintWriter writer = ctx.getResponse().getWriter();
                    writer.write("event: shutdown\ndata: {\"status\":\"server_shutdown\"}\n\n");
                    writer.flush();
                    ctx.complete();
                } catch (Exception ignored) {}
            }
        }
        channelSubscribers.clear();
        totalConnections.set(0);
        logger.info("EventBroadcaster shut down cleanly.");
    }
}
