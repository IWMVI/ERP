(() => {
    const input = document.querySelector("[data-document-identification]");
    const typeLabel = document.querySelector("[data-document-identification-type]");
    if (!input) return;

    const form = input.closest("form");
    const continueButton = form?.querySelector("button[type='submit']");

    const rawValue = () =>
        (input.value || "").toUpperCase().replace(/[^A-Z0-9]/g, "").slice(0, 14);

    const detectType = (raw) => {
        if (/^\d{11}$/.test(raw)) return "CPF";
        if (/^[A-Z0-9]{12}\d{2}$/.test(raw)) return "CNPJ";
        return null;
    };

    const update = () => {
        const type = detectType(rawValue());

        if (typeLabel) {
            typeLabel.textContent = type
                ? `${type} identificado. Clique em Continuar para validar.`
                : "Informe um CPF ou CNPJ completo.";
        }

        if (continueButton) {
            continueButton.disabled = !type;
            continueButton.setAttribute("aria-disabled", String(!type));
        }
    };

    input.addEventListener("input", update);
    update();
})();
