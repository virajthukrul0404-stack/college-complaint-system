// Service Worker for Campus Notice Board PWA
const CACHE_NAME = 'campus-board-v2';
const PRECACHE_URLS = [
  'offline.html',
  'assets/css/tokens.css',
  'assets/css/base.css',
  'assets/css/components.css',
  'assets/css/student.css',
  'assets/css/device.css',
  'assets/js/stamp.js'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      return cache.addAll(PRECACHE_URLS).catch(err => {
        console.warn('Pre-caching warning:', err);
      });
    }).then(() => self.skipWaiting())
  );
});

self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys => {
      return Promise.all(
        keys.filter(key => key !== CACHE_NAME).map(key => caches.delete(key))
      );
    }).then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', event => {
  const req = event.request;
  const url = new URL(req.url);

  // Bypass service worker for mutating requests, SSE streams, health checks, and attachment binaries
  if (req.method !== 'GET' ||
      url.pathname.endsWith('/events') ||
      url.pathname.includes('/healthz') ||
      url.pathname.includes('/attachment')) {
    return;
  }

  // HTML navigation requests: network first with offline fallback for cold-starts/sleep
  if (req.mode === 'navigate') {
    event.respondWith(
      fetch(req).catch(() => {
        return caches.match('offline.html').then(res => res || caches.match('/offline.html'));
      })
    );
    return;
  }

  // Static assets: cache first, fallback to network
  if (url.pathname.includes('/assets/')) {
    event.respondWith(
      caches.match(req).then(cached => {
        if (cached) return cached;
        return fetch(req).then(networkRes => {
          if (networkRes && networkRes.status === 200) {
            const clone = networkRes.clone();
            caches.open(CACHE_NAME).then(cache => cache.put(req, clone));
          }
          return networkRes;
        });
      })
    );
  }
});
