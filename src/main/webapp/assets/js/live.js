/**
 * Real-Time Server-Sent Events (SSE) Client
 * Handles connection, Last-Event-ID tracking, auto-reconnect with exponential backoff,
 * and visibility/online reconnection for mobile and desktop browsers.
 */

export function initLiveStream(channel, options = {}) {
  if (!window.EventSource) {
    console.warn("Server-Sent Events not supported by this browser.");
    return null;
  }

  const base = document.querySelector('meta[name="context-path"]')?.content || "";
  let eventSource = null;
  let retryDelay = 2000;
  const maxRetryDelay = 30000;
  let isExplicitlyClosed = false;

  function connect() {
    if (isExplicitlyClosed) return;
    if (eventSource) {
      try { eventSource.close(); } catch (e) {}
    }

    const url = `${base}/events?channel=${encodeURIComponent(channel)}`;
    console.log(`Connecting to SSE stream on channel: ${channel}`);
    eventSource = new EventSource(url);

    eventSource.addEventListener("open", () => {
      console.log(`SSE connection established for channel: ${channel}`);
      retryDelay = 2000;
      if (options.onConnect) options.onConnect();
    });

    eventSource.addEventListener("connected", (e) => {
      console.log("Handshake received:", e.data);
    });

    eventSource.addEventListener("statusUpdate", (e) => {
      try {
        const data = JSON.parse(e.data);
        console.log("Status update received:", data);
        if (options.onStatusUpdate) {
          options.onStatusUpdate(data);
        }
      } catch (err) {
        console.error("Error parsing statusUpdate event:", err);
      }
    });

    eventSource.addEventListener("notification", (e) => {
      try {
        const data = JSON.parse(e.data);
        console.log("Notification received:", data);
        if (options.onNotification) {
          options.onNotification(data);
        }
      } catch (err) {
        console.error("Error parsing notification event:", err);
      }
    });

    eventSource.addEventListener("newComplaint", (e) => {
      try {
        const data = JSON.parse(e.data);
        console.log("New complaint event received:", data);
        if (options.onNewComplaint) {
          options.onNewComplaint(data);
        }
      } catch (err) {
        console.error("Error parsing newComplaint event:", err);
      }
    });

    eventSource.onerror = (err) => {
      if (isExplicitlyClosed) return;
      console.warn(`SSE connection error on channel '${channel}'. Reconnecting in ${retryDelay / 1000}s...`);
      eventSource.close();
      setTimeout(connect, retryDelay);
      retryDelay = Math.min(retryDelay * 1.5, maxRetryDelay);
    };
  }

  // Auto reconnect on device wake / tab visibility change
  document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible" && !isExplicitlyClosed) {
      if (!eventSource || eventSource.readyState === EventSource.CLOSED) {
        console.log("Tab returned to foreground: reconnecting SSE...");
        connect();
      }
    }
  });

  // Auto reconnect when network comes back online
  window.addEventListener("online", () => {
    if (!isExplicitlyClosed) {
      console.log("Network online detected: reconnecting SSE...");
      retryDelay = 1000;
      connect();
    }
  });

  connect();

  return {
    close: () => {
      isExplicitlyClosed = true;
      if (eventSource) eventSource.close();
    }
  };
}
