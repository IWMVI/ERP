(() => {
    const STORAGE_KEY = "erp.sidebar.collapsed";
    const body = document.body;

    const applySidebarState = (collapsed) => {
        body.classList.toggle("sidebar-collapsed", collapsed);

        document.querySelectorAll("[data-sidebar-toggle]").forEach((button) => {
            const label = collapsed ? "Expandir menu" : "Minimizar menu";
            button.setAttribute("aria-label", label);
            button.setAttribute("title", label);
        });
    };

    const storedState = localStorage.getItem(STORAGE_KEY);
    applySidebarState(storedState === "true");

    document.addEventListener("click", (event) => {
        const toggle = event.target.closest("[data-sidebar-toggle]");
        if (toggle) {
            const collapsed = !body.classList.contains("sidebar-collapsed");
            localStorage.setItem(STORAGE_KEY, String(collapsed));
            applySidebarState(collapsed);
            return;
        }

        document.querySelectorAll(".user-dropdown[open]").forEach((dropdown) => {
            if (!dropdown.contains(event.target)) {
                dropdown.removeAttribute("open");
            }
        });
    });
})();
