/**
 * UI Utilities - Barcode Generation, Ticket Printing & Tactile Helpers
 */

export function generateSvgBarcode(containerId, code) {
  const container = document.getElementById(containerId);
  if (!container || !code) return;

  // Generate pseudo-code 128 vertical bars from tracking string
  const cleanCode = code.replace(/[^A-Za-z0-9]/g, '');
  let svg = `<svg viewBox="0 0 240 50" class="ticket-barcode-svg" xmlns="http://www.w3.org/2000/svg">`;
  svg += `<rect width="240" height="50" fill="transparent" />`;

  let x = 10;
  for (let i = 0; i < cleanCode.length; i++) {
    const charCode = cleanCode.charCodeAt(i);
    const w1 = (charCode % 3) + 1.5;
    const gap = (charCode % 2) + 1.5;
    const w2 = ((charCode * 3) % 4) + 1;

    svg += `<rect x="${x}" y="0" width="${w1}" height="45" fill="#1A1916" />`;
    x += w1 + gap;
    svg += `<rect x="${x}" y="0" width="${w2}" height="45" fill="#1A1916" />`;
    x += w2 + gap;
  }
  // End guards
  svg += `<rect x="${x}" y="0" width="3" height="45" fill="#1A1916" />`;
  svg += `</svg>`;

  container.innerHTML = svg;
}

export function setupPrintButton(buttonId) {
  const btn = document.getElementById(buttonId);
  if (btn) {
    btn.addEventListener('click', () => {
      window.print();
    });
  }
}
