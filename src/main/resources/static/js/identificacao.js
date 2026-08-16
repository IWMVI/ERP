(() => {
    const input = document.querySelector("[data-document-identification]");
    const typeLabel = document.querySelector("[data-document-identification-type]");
    if (!input) return;

    const form = input.closest("form");
    const continueButton = form?.querySelector("button[type='submit']");
    const CNPJ_FIRST_WEIGHTS = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
    const CNPJ_SECOND_WEIGHTS = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];

    const rawValue = () =>
        (input.value || "").toUpperCase().replace(/[^A-Z0-9]/g, "").slice(0, 14);

    const allSame = (value) => value.length > 0 && [...value].every((char) => char === value[0]);

    const cpfDigit = (base, initialWeight) => {
        const sum = [...base].reduce(
            (total, char, index) => total + Number(char) * (initialWeight - index),
            0,
        );
        const remainder = 11 - (sum % 11);
        return remainder >= 10 ? 0 : remainder;
    };

    const validCpf = (raw) => {
        if (!/^\d{11}$/.test(raw) || allSame(raw)) return false;
        return (
            cpfDigit(raw.slice(0, 9), 10) === Number(raw[9]) &&
            cpfDigit(raw.slice(0, 10), 11) === Number(raw[10])
        );
    };

    const cnpjCharValue = (char) => char.charCodeAt(0) - 48;

    const cnpjDigit = (base, weights) => {
        const sum = [...base].reduce(
            (total, char, index) => total + cnpjCharValue(char) * weights[index],
            0,
        );
        const remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    };

    const validCnpj = (raw) => {
        if (!/^[A-Z0-9]{12}\d{2}$/.test(raw) || allSame(raw)) return false;
        const first = cnpjDigit(raw.slice(0, 12), CNPJ_FIRST_WEIGHTS);
        const second = cnpjDigit(`${raw.slice(0, 12)}${first}`, CNPJ_SECOND_WEIGHTS);
        return first === Number(raw[12]) && second === Number(raw[13]);
    };

    const evaluate = (raw) => {
        if (/^\d{11}$/.test(raw)) {
            return { type: "CPF", valid: validCpf(raw) };
        }
        if (/^[A-Z0-9]{12}\d{2}$/.test(raw)) {
            return { type: "CNPJ", valid: validCnpj(raw) };
        }
        return { type: null, valid: false };
    };

    const update = () => {
        const raw = rawValue();
        const result = evaluate(raw);

        if (typeLabel) {
            typeLabel.classList.toggle("field-success", result.valid);
            typeLabel.classList.toggle("field-error", Boolean(result.type) && !result.valid);
            typeLabel.textContent = result.valid
                ? `${result.type} válido. Clique em Continuar.`
                : result.type
                  ? `${result.type} inválido. Confira o documento.`
                  : "Informe um CPF ou CNPJ completo.";
        }

        if (continueButton) {
            continueButton.disabled = !result.valid;
            continueButton.setAttribute("aria-disabled", String(!result.valid));
        }
    };

    input.addEventListener("input", update);
    update();
})();
