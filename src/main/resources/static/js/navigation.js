(() => {
  if (window.erpNavigationInitialized) return;
  window.erpNavigationInitialized = true;

  const STORAGE_KEY = "erp.sidebar.collapsed";
  const body = document.body;
  const MENU_GAP = 6;
  const VIEWPORT_MARGIN = 8;

  const ensureLayoutStylesheet = () => {
    if (document.querySelector("link[data-layout-stylesheet]")) return;

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
    document
      .querySelectorAll(".user-dropdown[open], .row-actions[open]")
      .forEach((menu) => {
        if (menu !== current) {
          menu.removeAttribute("open");
        }
      });
  };

  const positionRowActionsMenu = (details) => {
    const trigger = details.querySelector("summary");
    const menu = details.querySelector(".row-actions-menu");
    if (!trigger || !menu) return;

    menu.style.removeProperty("top");
    menu.style.removeProperty("left");
    menu.style.removeProperty("max-height");

    const triggerRect = trigger.getBoundingClientRect();
    const cardRect = details.closest(".registry-card")?.getBoundingClientRect();
    const boundary = {
      top: Math.max(VIEWPORT_MARGIN, cardRect?.top ?? VIEWPORT_MARGIN),
      right: Math.min(
        window.innerWidth - VIEWPORT_MARGIN,
        cardRect?.right ?? window.innerWidth - VIEWPORT_MARGIN,
      ),
      bottom: Math.min(
        window.innerHeight - VIEWPORT_MARGIN,
        cardRect?.bottom ?? window.innerHeight - VIEWPORT_MARGIN,
      ),
      left: Math.max(VIEWPORT_MARGIN, cardRect?.left ?? VIEWPORT_MARGIN),
    };
    const menuRect = menu.getBoundingClientRect();
    const spaceBelow = boundary.bottom - triggerRect.bottom - MENU_GAP;
    const spaceAbove = triggerRect.top - boundary.top - MENU_GAP;
    const openBelow = spaceBelow >= menuRect.height || spaceBelow >= spaceAbove;
    const availableHeight = Math.max(0, openBelow ? spaceBelow : spaceAbove);
    const top = openBelow
      ? triggerRect.bottom + MENU_GAP
      : triggerRect.top - Math.min(menuRect.height, availableHeight) - MENU_GAP;
    const left = Math.min(
      Math.max(boundary.left, triggerRect.right - menuRect.width),
      Math.max(boundary.left, boundary.right - menuRect.width),
    );

    menu.style.top = `${Math.max(boundary.top, top)}px`;
    menu.style.left = `${left}px`;
    menu.style.maxHeight = `${availableHeight}px`;
    menu.dataset.placement = openBelow ? "bottom" : "top";
  };

  ensureLayoutStylesheet();

  const storedState = localStorage.getItem(STORAGE_KEY);
  applySidebarState(storedState === "true");

  document.addEventListener(
    "toggle",
    (event) => {
      const menu = event.target;
      if (
        menu instanceof HTMLDetailsElement &&
        menu.open &&
        menu.matches(".user-dropdown, .row-actions")
      ) {
        closeMenusExcept(menu);
        if (menu.matches(".row-actions")) {
          positionRowActionsMenu(menu);
        }
      }
    },
    true,
  );

  window.addEventListener("resize", () => {
    document
      .querySelectorAll(".row-actions[open]")
      .forEach(positionRowActionsMenu);
  });

  document.querySelector(".content")?.addEventListener(
    "scroll",
    () => {
      document
        .querySelectorAll(".row-actions[open]")
        .forEach(positionRowActionsMenu);
    },
    { passive: true },
  );

  document.addEventListener("click", (event) => {
    const toggle = event.target.closest("[data-sidebar-toggle]");
    if (toggle) {
      const collapsed = !body.classList.contains("sidebar-collapsed");
      localStorage.setItem(STORAGE_KEY, String(collapsed));
      applySidebarState(collapsed);
      return;
    }

    document
      .querySelectorAll(".user-dropdown[open], .row-actions[open]")
      .forEach((menu) => {
        if (!menu.contains(event.target)) {
          menu.removeAttribute("open");
        }
      });
  });

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      document
        .querySelectorAll(".user-dropdown[open], .row-actions[open]")
        .forEach((menu) => {
          menu.removeAttribute("open");
        });
    }
  });
})();
