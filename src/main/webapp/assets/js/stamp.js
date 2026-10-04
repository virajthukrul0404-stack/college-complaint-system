/**
 * Stamp Animation & Utility
 */

export function applyStamp(element, status) {
  if (!element) return;

  const normalized = (status || "").toLowerCase().replace(/\s+/g, '-');
  element.className = `stamp stamp-lg stamp-${normalized} stamp-animate`;
  element.textContent = status.toUpperCase();

  // Remove animation class after playback so it can re-trigger on subsequent updates
  setTimeout(() => {
    element.classList.remove('stamp-animate');
  }, 400);
}
