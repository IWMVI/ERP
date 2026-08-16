(() => {
  if (window.erpDropdownsInitialized) return;
  window.erpDropdownsInitialized = true;

  const instances = new Map();
  let sequence = 0;
  let typeahead = "";
  let typeaheadTimer;

  const normalize = (value) =>
    value
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .toLocaleLowerCase("pt-BR");

  const closeAll = (except) => {
    instances.forEach((instance) => {
      if (instance !== except) instance.close(false);
    });
  };

  const enhance = (select) => {
    if (
      !(select instanceof HTMLSelectElement) ||
      select.multiple ||
      select.dataset.nativeSelect === "true" ||
      select.dataset.customSelectInitialized === "true"
    ) {
      return;
    }

    sequence += 1;
    const instanceId = `erp-select-${sequence}`;
    const wrapper = document.createElement("div");
    const trigger = document.createElement("button");
    const value = document.createElement("span");
    const menu = document.createElement("div");

    wrapper.className = "custom-select";
    wrapper.dataset.customSelect = "";
    trigger.type = "button";
    trigger.id = `${instanceId}-trigger`;
    trigger.className = "custom-select-trigger";
    trigger.setAttribute("role", "combobox");
    trigger.setAttribute("aria-haspopup", "listbox");
    trigger.setAttribute("aria-expanded", "false");
    trigger.setAttribute("aria-controls", `${instanceId}-options`);
    trigger.setAttribute("aria-autocomplete", "none");
    value.className = "custom-select-value";
    menu.id = `${instanceId}-options`;
    menu.className = "custom-select-options";
    menu.setAttribute("role", "listbox");
    menu.hidden = true;
    trigger.appendChild(value);
    wrapper.append(trigger, menu);
    select.insertAdjacentElement("afterend", wrapper);

    select.dataset.customSelectInitialized = "true";
    select.classList.add("custom-select-native");
    select.tabIndex = -1;
    select.setAttribute("aria-hidden", "true");

    const originalId = select.id;
    const explicitLabel = originalId
      ? document.querySelector(`label[for="${CSS.escape(originalId)}"]`)
      : null;
    const wrappingLabel = select.closest("label");
    if (explicitLabel) {
      explicitLabel.htmlFor = trigger.id;
      if (!explicitLabel.id) explicitLabel.id = `${instanceId}-label`;
      trigger.setAttribute("aria-labelledby", explicitLabel.id);
      select.dataset.customSelectLabelId = explicitLabel.id;
    } else if (wrappingLabel) {
      const labelText = Array.from(wrappingLabel.childNodes)
        .filter((node) => node.nodeType === Node.TEXT_NODE)
        .map((node) => node.textContent.trim())
        .filter(Boolean)
        .join(" ");
      wrappingLabel.htmlFor = trigger.id;
      trigger.setAttribute("aria-label", labelText || "Selecionar opção");
    } else if (select.getAttribute("aria-label")) {
      trigger.setAttribute("aria-label", select.getAttribute("aria-label"));
    } else {
      const inferredLabel = select.name
        ?.replace(/Id$/, "")
        .replace(/([a-z])([A-Z])/g, "$1 $2");
      trigger.setAttribute("aria-label", inferredLabel || "Selecionar opção");
    }

    const describedBy = select.getAttribute("aria-describedby");
    if (describedBy) trigger.setAttribute("aria-describedby", describedBy);

    let optionElements = [];
    let activeIndex = -1;

    const enabledIndexes = () =>
      optionElements
        .map((entry, index) => ({ entry, index }))
        .filter(({ entry }) => !entry.option.disabled && !entry.option.hidden)
        .map(({ index }) => index);

    const selectedIndex = () =>
      optionElements.findIndex(({ option }) => option.selected);

    const setActive = (index, scroll = true) => {
      if (
        !Number.isInteger(index) ||
        index < 0 ||
        index >= optionElements.length
      )
        return;
      const entry = optionElements[index];
      if (entry.option.disabled || entry.option.hidden) return;

      optionElements.forEach(({ element }) =>
        element.classList.remove("is-active"),
      );
      activeIndex = index;
      entry.element.classList.add("is-active");
      trigger.setAttribute("aria-activedescendant", entry.element.id);
      if (scroll) entry.element.scrollIntoView({ block: "nearest" });
    };

    const positionMenu = () => {
      if (menu.hidden) return;
      const margin = 8;
      const gap = 5;
      const rect = trigger.getBoundingClientRect();
      const availableBelow = window.innerHeight - rect.bottom - margin - gap;
      const availableAbove = rect.top - margin - gap;
      const openAbove = availableBelow < 180 && availableAbove > availableBelow;
      const available = Math.max(
        120,
        openAbove ? availableAbove : availableBelow,
      );

      menu.style.left = `${Math.max(margin, rect.left)}px`;
      menu.style.width = `${Math.min(rect.width, window.innerWidth - margin * 2)}px`;
      menu.style.maxHeight = `${Math.min(280, available)}px`;
      menu.style.top = openAbove ? "auto" : `${rect.bottom + gap}px`;
      menu.style.bottom = openAbove
        ? `${window.innerHeight - rect.top + gap}px`
        : "auto";
      menu.dataset.placement = openAbove ? "top" : "bottom";
    };

    const close = (restoreFocus = false) => {
      if (menu.hidden) return;
      menu.hidden = true;
      wrapper.classList.remove("is-open");
      trigger.setAttribute("aria-expanded", "false");
      trigger.removeAttribute("aria-activedescendant");
      activeIndex = -1;
      if (restoreFocus) trigger.focus();
    };

    const open = () => {
      if (select.disabled || optionElements.length === 0) return;
      closeAll(instance);
      typeahead = "";
      menu.hidden = false;
      wrapper.classList.add("is-open");
      trigger.setAttribute("aria-expanded", "true");
      positionMenu();
      const current = selectedIndex();
      const first = enabledIndexes()[0] ?? -1;
      setActive(
        current >= 0 && !select.options[current]?.disabled ? current : first,
      );
    };

    const choose = (index) => {
      const entry = optionElements[index];
      if (!entry || entry.option.disabled || entry.option.hidden) return;

      const changed = select.value !== entry.option.value;
      select.value = entry.option.value;
      sync();
      close(true);
      if (changed) {
        select.dispatchEvent(new Event("input", { bubbles: true }));
        select.dispatchEvent(new Event("change", { bubbles: true }));
      }
    };

    const move = (direction) => {
      const indexes = enabledIndexes();
      if (indexes.length === 0) return;
      const currentPosition = indexes.indexOf(activeIndex);
      const nextPosition =
        currentPosition < 0
          ? direction > 0
            ? 0
            : indexes.length - 1
          : (currentPosition + direction + indexes.length) % indexes.length;
      setActive(indexes[nextPosition]);
    };

    const search = (key) => {
      window.clearTimeout(typeaheadTimer);
      typeahead += normalize(key);
      typeaheadTimer = window.setTimeout(() => {
        typeahead = "";
      }, 700);

      const indexes = enabledIndexes();
      const start = Math.max(0, indexes.indexOf(activeIndex) + 1);
      const ordered = [...indexes.slice(start), ...indexes.slice(0, start)];
      const match = ordered.find((index) =>
        normalize(optionElements[index].option.textContent).startsWith(
          typeahead,
        ),
      );
      if (match !== undefined) setActive(match);
    };

    const buildOptions = () => {
      menu.replaceChildren();
      optionElements = [];

      Array.from(select.children).forEach((child) => {
        if (child instanceof HTMLOptGroupElement) {
          const group = document.createElement("div");
          group.className = "custom-select-group";
          group.textContent = child.label;
          group.setAttribute("role", "presentation");
          menu.appendChild(group);
          Array.from(child.children).forEach((option) => addOption(option));
        } else if (child instanceof HTMLOptionElement) {
          addOption(child);
        }
      });
      sync();
    };

    const addOption = (option) => {
      const index = optionElements.length;
      const element = document.createElement("div");
      element.id = `${instanceId}-option-${index}`;
      element.className = "custom-select-option";
      element.setAttribute("role", "option");
      element.dataset.optionIndex = String(index);
      element.textContent = option.textContent;
      if (option.disabled) element.setAttribute("aria-disabled", "true");
      if (option.hidden) element.hidden = true;
      menu.appendChild(element);
      optionElements.push({ option, element });
    };

    function sync() {
      const selected = select.selectedOptions[0];
      value.textContent = selected?.textContent?.trim() || "Selecione";
      trigger.disabled = select.disabled;
      trigger.required = select.required;
      trigger.classList.toggle(
        "is-placeholder",
        !selected || selected.value === "",
      );
      wrapper.classList.toggle("is-disabled", select.disabled);
      optionElements.forEach(({ option, element }) => {
        const isSelected = option.selected;
        element.classList.toggle("is-selected", isSelected);
        element.setAttribute("aria-selected", String(isSelected));
      });
      const showInvalid =
        wrapper.classList.contains("is-invalid") ||
        select.getAttribute("aria-invalid") === "true";
      if (showInvalid) {
        trigger.setAttribute("aria-invalid", "true");
      } else {
        trigger.removeAttribute("aria-invalid");
      }
      if (!select.matches(":invalid")) wrapper.classList.remove("is-invalid");
    }

    const instance = { close, open, positionMenu, select, sync };
    instances.set(select, instance);

    trigger.addEventListener("click", () => {
      if (menu.hidden) open();
      else close(false);
    });

    trigger.addEventListener("keydown", (event) => {
      if (event.key === "ArrowDown" || event.key === "ArrowUp") {
        event.preventDefault();
        if (menu.hidden) open();
        else move(event.key === "ArrowDown" ? 1 : -1);
        return;
      }
      if (event.key === "Home" && !menu.hidden) {
        event.preventDefault();
        setActive(enabledIndexes()[0]);
        return;
      }
      if (event.key === "End" && !menu.hidden) {
        event.preventDefault();
        const indexes = enabledIndexes();
        setActive(indexes[indexes.length - 1]);
        return;
      }
      if ((event.key === "Enter" || event.key === " ") && !menu.hidden) {
        event.preventDefault();
        choose(activeIndex);
        return;
      }
      if (event.key === "Escape" && !menu.hidden) {
        event.preventDefault();
        close(true);
        return;
      }
      if (event.key === "Tab") {
        close(false);
        return;
      }
      if (
        event.key.length === 1 &&
        !event.ctrlKey &&
        !event.metaKey &&
        !event.altKey
      ) {
        if (menu.hidden) open();
        search(event.key);
      }
    });

    menu.addEventListener("pointermove", (event) => {
      const option = event.target.closest(".custom-select-option");
      if (!option || option.getAttribute("aria-disabled") === "true") return;
      setActive(Number(option.dataset.optionIndex), false);
    });

    menu.addEventListener("click", (event) => {
      const option = event.target.closest(".custom-select-option");
      if (!option) return;
      event.preventDefault();
      event.stopPropagation();
      choose(Number(option.dataset.optionIndex));
    });

    select.addEventListener("change", sync);
    select.addEventListener("invalid", () => {
      wrapper.classList.add("is-invalid");
      trigger.setAttribute("aria-invalid", "true");
      window.setTimeout(() => trigger.focus(), 0);
    });
    select.form?.addEventListener("reset", () => window.setTimeout(sync, 0));

    new MutationObserver(buildOptions).observe(select, {
      attributes: true,
      childList: true,
      subtree: true,
      attributeFilter: ["disabled", "hidden", "label", "selected"],
    });

    buildOptions();
  };

  const enhanceAll = (root = document) => {
    if (root instanceof HTMLSelectElement) enhance(root);
    root.querySelectorAll?.("select").forEach(enhance);
  };

  enhanceAll();

  new MutationObserver((mutations) => {
    mutations.forEach(({ addedNodes }) => {
      addedNodes.forEach((node) => {
        if (node instanceof Element) enhanceAll(node);
      });
    });
  }).observe(document.body, { childList: true, subtree: true });

  document.addEventListener("pointerdown", (event) => {
    instances.forEach((instance) => {
      const wrapper = instance.select.nextElementSibling;
      if (wrapper && !wrapper.contains(event.target)) instance.close(false);
    });
  });

  const repositionOpenMenus = () => {
    instances.forEach((instance) => instance.positionMenu());
  };
  window.addEventListener("resize", repositionOpenMenus);
  window.addEventListener("scroll", repositionOpenMenus, {
    capture: true,
    passive: true,
  });
})();
