(() => {
    const STORAGE_KEY = "erp.sidebar.collapsed";
    const body = document.body;

    const ensureLayoutStylesheet = () => {
        if (document.querySelector('link[data-layout-stylesheet]')) return;

        const link = document.createElement("link");
        link.rel = "stylesheet";
        link.href = "/css/layout.css";
        link.dataset.layoutStylesheet = "true";
        document.head.appendChild(link);
    };

    const applySidebarState = (collapsed) => {
        body.classList.toggle("sidebar-collapsed", collapsed);

        document.querySelectorAll("[data-sidebar-toggle]").forEach((button) => {
            const label = collapsed ? "Expandir menu" : "Minimizar menu";
            button.setAttribute("aria-label", label);
            button.setAttribute("title", label);
        });
    };

    const closeMenusExcept = (current) => {
        document.querySelectorAll(".user-dropdown[open], .row-actions[open]").forEach((menu) => {
            if (menu !== current) {
                menu.removeAttribute("open");
            }
        });
    };

    ensureLayoutStylesheet();

    const storedState = localStorage.getItem(STORAGE_KEY);
    applySidebarState(storedState === "true");

    document.addEventListener("toggle", (event) => {
        const menu = event.target;
        if (menu instanceof HTMLDetailsElement && menu.open && menu.matches(".user-dropdown, .row-actions")) {
            closeMenusExcept(menu);
        }
    }, true);

    document.addEventListener("click", (event) => {
        const toggle = event.target.closest("[data-sidebar-toggle]");
        if (toggle) {
            const collapsed = !body.classList.contains("sidebar-collapsed");
            localStorage.setItem(STORAGE_KEY, String(collapsed));
            applySidebarState(collapsed);
            return;
        }

        document.querySelectorAll(".user-dropdown[open], .row-actions[open]").forEach((menu) => {
            if (!menu.contains(event.target)) {
                menu.removeAttribute("open");
            }
        });
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            document.querySelectorAll(".user-dropdown[open], .row-actions[open]").forEach((menu) => {
                menu.removeAttribute("open");
            });
        }
    });
})();
