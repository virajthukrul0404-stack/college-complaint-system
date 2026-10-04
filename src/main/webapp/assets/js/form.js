/**
 * Form Validation and Interactive Slip Behaviors
 */

export function setupComplaintForm() {
  const form = document.querySelector("#complaintForm");
  if (!form) return;

  const fileInput = document.querySelector("#attachmentInput");
  const fileError = document.querySelector("#attachmentError");
  const anonCheckbox = document.querySelector("#anonToggle");
  const nameInput = document.querySelector("#studentName");
  const rollInput = document.querySelector("#rollNumber");

  if (fileInput) {
    fileInput.addEventListener("change", (e) => {
      const file = e.target.files[0];
      if (file) {
        if (file.size > 2 * 1024 * 1024) {
          if (fileError) fileError.textContent = "Attachment exceeds 2 MB limit.";
          fileInput.value = "";
        } else {
          const ext = file.name.split('.').pop().toLowerCase();
          if (!['jpg', 'jpeg', 'png', 'webp'].includes(ext)) {
            if (fileError) fileError.textContent = "Only JPG, PNG, and WEBP images allowed.";
            fileInput.value = "";
          } else {
            if (fileError) fileError.textContent = "";
          }
        }
      }
    });
  }

  if (anonCheckbox) {
    anonCheckbox.addEventListener("change", (e) => {
      const isAnon = e.target.checked;
      const note = document.querySelector("#anonNote");
      if (note) {
        note.textContent = isAnon 
          ? "Your name and roll number will be masked from administrators. Only your contact email is kept for resolution alerts."
          : "Your name and roll number will be visible to department administrators.";
      }
    });
  }

  // Ruled Paper Perforation Submit Animation
  form.addEventListener("submit", (e) => {
    const slip = document.querySelector(".complaint-slip");
    if (slip && !window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      slip.style.transition = "transform 0.4s ease-in, opacity 0.4s ease-in";
      slip.style.transform = "translateY(30px) rotate(2deg)";
      slip.style.opacity = "0.7";
    }
  });
}
