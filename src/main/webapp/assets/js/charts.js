/**
 * Flat Pen-and-Ink Chart Configuration for Admin Analytics
 */

export function renderTrendChart(canvasId, labels, values) {
  const canvas = document.getElementById(canvasId);
  if (!canvas || !window.Chart) return;

  const ctx = canvas.getContext('2d');

  new window.Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [{
        label: 'Filed Complaints',
        data: values,
        borderColor: '#1A1916',
        borderWidth: 3,
        backgroundColor: 'rgba(244, 196, 48, 0.25)',
        fill: true,
        tension: 0.1,
        pointBackgroundColor: '#E5481B',
        pointBorderColor: '#1A1916',
        pointBorderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          backgroundColor: '#1A1916',
          titleFont: { family: "'IBM Plex Mono', monospace", size: 12 },
          bodyFont: { family: "'IBM Plex Mono', monospace", size: 12 },
          padding: 10,
          cornerRadius: 4,
          displayColors: false
        }
      },
      scales: {
        x: {
          grid: {
            color: 'rgba(26, 25, 22, 0.08)',
            borderDash: [4, 4]
          },
          ticks: {
            font: { family: "'IBM Plex Mono', monospace", size: 10 },
            color: '#6B6457',
            maxRotation: 45
          }
        },
        y: {
          beginAtZero: true,
          grid: {
            color: 'rgba(26, 25, 22, 0.08)',
            borderDash: [4, 4]
          },
          ticks: {
            font: { family: "'IBM Plex Mono', monospace", size: 11 },
            color: '#6B6457',
            precision: 0
          }
        }
      }
    }
  });
}
