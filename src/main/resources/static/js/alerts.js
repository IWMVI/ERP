(() => {
    const closeToast = (toast) => {
        if (!toast || toast.classList.contains("is-leaving")) return;
        toast.classList.add("is-leaving");
        window.setTimeout(() => toast.remove(), 170);
    };

    document.querySelectorAll("[data-toast]").forEach((toast) => {
        toast.querySelector("[data-toast-close]")?.addEventListener("click", () => closeToast(toast));

        if (!toast.hasAttribute("data-toast-persistent")) {
            window.setTimeout(() => closeToast(toast), 5000);
        }
    });
})();
