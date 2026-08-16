(() => {
    const input = document.querySelector("[data-document-identification]");
    const typeLabel = document.querySelector("[data-document-identification-type]");
    if (!input) return;

    const form = input.closest("form");
    let timer = null;
    let submittedValue = "";

    const rawValue = () =>
        (input.value || "").toUpperCase().replace(/[^A-Z0-9]/g, "").slice(0, 14);

    const detectType = (raw) => {
        if (/^\d{11}$/.test(raw)) return "CPF";
        if (/^[A-Z0-9]{12}\d{2}$/.test(raw)) return "CNPJ";
        return null;
    };

    const update = () => {
        const raw = rawValue();
        const type = detectType(raw);

        if (typeLabel) {
            typeLabel.textContent = type
                ? `${type} identificado. Consultando...`
                : "Informe CPF ou CNPJ";
        }

        window.clearTimeout(timer);
        if (!type || raw === submittedValue || !form) return;

        timer = window.setTimeout(() => {
            submittedValue = raw;
            form.requestSubmit();
        }, 450);
    };

    input.addEventListener("input", update);
    update();
})();
