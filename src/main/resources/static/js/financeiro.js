(() => {
  document.querySelectorAll("[data-modal-open]").forEach((button) => {
    button.addEventListener("click", () => {
      const dialog = document.getElementById(button.dataset.modalOpen);
      if (dialog instanceof HTMLDialogElement && !dialog.open) dialog.showModal();
    });
  });

  document.querySelectorAll("[data-modal-close]").forEach((button) => {
    button.addEventListener("click", () => {
      const dialog = button.closest("dialog");
      if (dialog) dialog.close();
    });
  });

  document.querySelectorAll("dialog[data-modal-backdrop-close]").forEach((dialog) => {
    dialog.addEventListener("click", (event) => {
      if (event.target === dialog) dialog.close();
    });
  });
})();
