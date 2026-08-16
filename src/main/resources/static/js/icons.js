(() => {
  if (window.erpIconsInitialized) return;
  window.erpIconsInitialized = true;

  const init = () => {
    if (typeof lucide === "undefined" || typeof lucide.createIcons !== "function") {
      return;
    }
    lucide.createIcons();
  };

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
