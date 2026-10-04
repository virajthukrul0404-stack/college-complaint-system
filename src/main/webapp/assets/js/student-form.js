/* ==========================================================================
   Student Complaint Form & Adaptive Client Interactions
   ========================================================================== */

(function () {
  'use strict';

  // --- 1. LocalStorage Draft Management ---
  const DRAFT_KEY = 'campus_complaint_draft';

  function saveDraft() {
    const draft = {
      category: document.getElementById('field-category')?.value || '',
      priority: document.querySelector('input[name="priority"]:checked')?.value || 'Medium',
      subject: document.getElementById('field-subject')?.value || '',
      description: document.getElementById('field-description')?.value || '',
      isAnonymous: document.getElementById('field-anonymous')?.checked || false,
      timestamp: Date.now()
    };
    try {
      localStorage.setItem(DRAFT_KEY, JSON.stringify(draft));
    } catch (e) {
      console.warn('Could not save draft to localStorage', e);
    }
  }

  function loadDraft() {
    try {
      const saved = localStorage.getItem(DRAFT_KEY);
      if (!saved) return;
      const draft = JSON.parse(saved);

      const catField = document.getElementById('field-category');
      if (catField && draft.category) catField.value = draft.category;

      const prioRadio = document.querySelector(`input[name="priority"][value="${draft.priority}"]`);
      if (prioRadio) prioRadio.checked = true;

      const subField = document.getElementById('field-subject');
      if (subField && draft.subject) subField.value = draft.subject;

      const descField = document.getElementById('field-description');
      if (descField && draft.description) descField.value = draft.description;

      const anonField = document.getElementById('field-anonymous');
      if (anonField && typeof draft.isAnonymous === 'boolean') anonField.checked = draft.isAnonymous;

      updateLiveReceipt();
      showDraftNotice();
    } catch (e) {
      console.warn('Could not restore draft', e);
    }
  }

  function clearDraft() {
    try {
      localStorage.removeItem(DRAFT_KEY);
    } catch (ignored) {}
  }

  function showDraftNotice() {
    const banner = document.getElementById('draft-restored-banner');
    if (banner) {
      banner.style.display = 'block';
      setTimeout(() => { banner.style.display = 'none'; }, 4000);
    }
  }

  // --- 2. Live Receipt Preview for Desktop ---
  function updateLiveReceipt() {
    const receiptSubject = document.getElementById('receipt-preview-subject');
    const receiptCategory = document.getElementById('receipt-preview-category');
    const receiptPriority = document.getElementById('receipt-preview-priority');
    const receiptComplainant = document.getElementById('receipt-preview-complainant');

    const subVal = document.getElementById('field-subject')?.value;
    const catVal = document.getElementById('field-category')?.value;
    const prioVal = document.querySelector('input[name="priority"]:checked')?.value || 'Medium';
    const isAnon = document.getElementById('field-anonymous')?.checked;
    const studentName = document.getElementById('student-name-holder')?.value || 'Student';

    if (receiptSubject) receiptSubject.textContent = subVal && subVal.trim().length > 0 ? subVal : '[Subject pending]';
    if (receiptCategory) receiptCategory.textContent = catVal && catVal.trim().length > 0 ? catVal : 'General';
    if (receiptPriority) {
      receiptPriority.textContent = prioVal;
      receiptPriority.className = 'priority-badge priority-' + prioVal.toLowerCase();
    }
    if (receiptComplainant) {
      receiptComplainant.textContent = isAnon ? 'Anonymous Student (Masked)' : studentName;
    }
  }

  // --- 3. Mobile Step Flow ---
  let currentStep = 1;
  const totalSteps = 4;

  function goToStep(step) {
    if (step < 1 || step > totalSteps) return;

    // Validate current step before advancing on mobile
    if (step > currentStep && !validateStep(currentStep)) {
      return;
    }

    currentStep = step;

    // Update step dots
    for (let i = 1; i <= totalSteps; i++) {
      const dot = document.getElementById('step-dot-' + i);
      const panel = document.getElementById('step-panel-' + i);
      if (dot) {
        dot.classList.remove('active', 'completed');
        if (i === currentStep) dot.classList.add('active');
        else if (i < currentStep) dot.classList.add('completed');
      }
      if (panel) {
        panel.classList.toggle('active', i === currentStep);
      }
    }

    // Populate review summary on step 4
    if (currentStep === 4) {
      populateReview();
    }

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function validateStep(step) {
    clearStepErrors();
    let valid = true;

    if (step === 1) {
      const cat = document.getElementById('field-category')?.value;
      if (!cat || cat.trim() === '') {
        showError('field-category', 'Please choose a category.');
        valid = false;
      }
    } else if (step === 2) {
      const sub = document.getElementById('field-subject')?.value;
      const desc = document.getElementById('field-description')?.value;
      if (!sub || sub.trim().length < 5) {
        showError('field-subject', 'Subject must be at least 5 characters.');
        valid = false;
      }
      if (!desc || desc.trim().length < 10) {
        showError('field-description', 'Description must be at least 10 characters.');
        valid = false;
      }
    }
    return valid;
  }

  function showError(fieldId, msg) {
    const field = document.getElementById(fieldId);
    if (!field) return;
    field.style.borderColor = 'var(--color-signal-red)';
    const errSpan = document.createElement('span');
    errSpan.className = 'field-error-msg';
    errSpan.style.color = 'var(--color-signal-red)';
    errSpan.style.fontFamily = 'var(--font-mono)';
    errSpan.style.fontSize = '0.78rem';
    errSpan.style.display = 'block';
    errSpan.style.marginTop = '4px';
    errSpan.textContent = msg;
    field.parentNode.appendChild(errSpan);
  }

  function clearStepErrors() {
    document.querySelectorAll('.field-error-msg').forEach(el => el.remove());
    document.querySelectorAll('input, select, textarea').forEach(el => {
      el.style.borderColor = '';
    });
  }

  function populateReview() {
    const revCategory = document.getElementById('rev-category');
    const revPriority = document.getElementById('rev-priority');
    const revSubject = document.getElementById('rev-subject');
    const revDescription = document.getElementById('rev-description');
    const revAnon = document.getElementById('rev-anon');

    if (revCategory) revCategory.textContent = document.getElementById('field-category')?.value || 'Not selected';
    if (revPriority) revPriority.textContent = document.querySelector('input[name="priority"]:checked')?.value || 'Medium';
    if (revSubject) revSubject.textContent = document.getElementById('field-subject')?.value || '';
    if (revDescription) revDescription.textContent = document.getElementById('field-description')?.value || '';
    if (revAnon) revAnon.textContent = document.getElementById('field-anonymous')?.checked ? 'Yes (Identity Hidden)' : 'No (Public Roll)';
  }

  // --- 4. Camera & Gallery Image Compression (Canvas API, max 1600px) ---
  function handleImageUpload(input) {
    const file = input.files && input.files[0];
    if (!file) return;

    if (!file.type.match('image.*')) {
      alert('Only JPEG, PNG, or WebP image attachments are supported.');
      input.value = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = function (e) {
      const img = new Image();
      img.onload = function () {
        const MAX_WIDTH = 1600;
        const MAX_HEIGHT = 1600;
        let width = img.width;
        let height = img.height;

        if (width > height) {
          if (width > MAX_WIDTH) {
            height *= MAX_WIDTH / width;
            width = MAX_WIDTH;
          }
        } else {
          if (height > MAX_HEIGHT) {
            width *= MAX_HEIGHT / height;
            height = MAX_HEIGHT;
          }
        }

        const canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);

        // Show preview thumbnail
        showImagePreview(canvas.toDataURL('image/jpeg', 0.85));
      };
      img.src = e.target.result;
    };
    reader.readAsDataURL(file);
  }

  function showImagePreview(dataUrl) {
    const previewContainer = document.getElementById('image-preview-area');
    if (!previewContainer) return;
    previewContainer.innerHTML = `
      <div class="image-preview-box">
        <img src="${dataUrl}" alt="Attachment preview" />
        <button type="button" class="remove-img-btn" id="btn-remove-preview" title="Remove photo">✕</button>
      </div>
    `;
    document.getElementById('btn-remove-preview')?.addEventListener('click', function () {
      previewContainer.innerHTML = '';
      const fileInput = document.getElementById('file-attachment');
      if (fileInput) fileInput.value = '';
    });
  }

  // --- 5. Global Keyboard Shortcuts & Initialization ---
  document.addEventListener('DOMContentLoaded', function () {
    // Draft restore
    loadDraft();

    // Listen for draft changes
    const form = document.getElementById('complaint-form');
    if (form) {
      form.addEventListener('input', () => {
        saveDraft();
        updateLiveReceipt();
      });
      form.addEventListener('change', () => {
        saveDraft();
        updateLiveReceipt();
      });
      form.addEventListener('submit', () => {
        clearDraft();
      });
    }

    // Step buttons
    document.getElementById('btn-step-1-next')?.addEventListener('click', () => goToStep(2));
    document.getElementById('btn-step-2-back')?.addEventListener('click', () => goToStep(1));
    document.getElementById('btn-step-2-next')?.addEventListener('click', () => goToStep(3));
    document.getElementById('btn-step-3-back')?.addEventListener('click', () => goToStep(2));
    document.getElementById('btn-step-3-next')?.addEventListener('click', () => goToStep(4));
    document.getElementById('btn-step-4-back')?.addEventListener('click', () => goToStep(3));

    // File input change
    const fileInput = document.getElementById('file-attachment');
    if (fileInput) {
      fileInput.addEventListener('change', function () {
        handleImageUpload(this);
      });
    }

    // Drag and drop zone
    const dropZone = document.getElementById('file-drop-target');
    if (dropZone && fileInput) {
      ['dragenter', 'dragover'].forEach(eventName => {
        dropZone.addEventListener(eventName, e => {
          e.preventDefault();
          e.stopPropagation();
          dropZone.classList.add('dragover');
        });
      });
      ['dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, e => {
          e.preventDefault();
          e.stopPropagation();
          dropZone.classList.remove('dragover');
        });
      });
      dropZone.addEventListener('drop', e => {
        if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
          fileInput.files = e.dataTransfer.files;
          handleImageUpload(fileInput);
        }
      });
    }

    // Keyboard shortcuts: 'N' for new complaint, '/' for search
    document.addEventListener('keydown', function (e) {
      if (['INPUT', 'TEXTAREA', 'SELECT'].includes(document.activeElement.tagName)) {
        return;
      }
      if (e.key === 'n' || e.key === 'N') {
        const newBtn = document.querySelector('a[href*="/student/complaint/new"]');
        if (newBtn) window.location.href = newBtn.href;
      } else if (e.key === '/') {
        const searchBox = document.getElementById('search-complaints-box');
        if (searchBox) {
          e.preventDefault();
          searchBox.focus();
        }
      }
    });

    // Network & Visibility Auto-Reconnect for SSE
    window.addEventListener('online', function () {
      console.log('Network restored: verifying live updates stream');
      if (window.reconnectEventStream) {
        window.reconnectEventStream();
      }
    });

    document.addEventListener('visibilitychange', function () {
      if (!document.hidden && window.reconnectEventStream) {
        window.reconnectEventStream();
      }
    });
  });

  // Register service worker if supported
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      navigator.serviceWorker.register('/sw.js').catch(err => {
        console.debug('ServiceWorker registration note:', err);
      });
    });
  }

})();
