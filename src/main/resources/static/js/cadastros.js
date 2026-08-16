(() => {
    const digits = (value) => (value || "").replace(/\D/g, "");

    const maskCpfCnpj = (value) => {
        const raw = digits(value).slice(0, 14);
        if (raw.length <= 11) {
            return raw
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d)/, "$1.$2")
                .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
        }
        return raw
            .replace(/(\d{2})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d)/, "$1/$2")
            .replace(/(\d{4})(\d{1,2})$/, "$1-$2");
    };

    const maskCep = (value) => digits(value).slice(0, 8).replace(/(\d{5})(\d)/, "$1-$2");

    const maskPhone = (value) => {
        const raw = digits(value).slice(0, 11);
        if (raw.length <= 10) {
            return raw
                .replace(/(\d{2})(\d)/, "($1) $2")
                .replace(/(\d{4})(\d)/, "$1-$2");
        }
        return raw
            .replace(/(\d{2})(\d)/, "($1) $2")
            .replace(/(\d{5})(\d)/, "$1-$2");
    };

    const maskGtin = (value) => digits(value).slice(0, 14);

    const formatters = {
        documento: maskCpfCnpj,
        cep: maskCep,
        telefone: maskPhone,
        celular: maskPhone,
        gtin: maskGtin,
    };

    document.querySelectorAll("[data-mask]").forEach((input) => {
        const formatter = formatters[input.dataset.mask];
        if (!formatter) return;

        const apply = () => {
            input.value = formatter(input.value);
        };

        input.addEventListener("input", apply);
        apply();
    });

    document.querySelectorAll("[data-format]").forEach((element) => {
        const formatter = formatters[element.dataset.format];
        if (formatter) {
            element.textContent = formatter(element.textContent ?? "");
        }
    });

    const setIfPresent = (name, value, formatter) => {
        if (value === null || value === undefined || value === "") return;
        const input = document.querySelector(`[name="${name}"]`);
        if (!input) return;
        input.value = formatter ? formatter(String(value)) : value;
        input.dispatchEvent(new Event("input", { bubbles: true }));
    };

    const showLookupState = (input, message, type = "info") => {
        const target = input.closest(".field-group")?.querySelector("[data-lookup-status]");
        if (!target) return;

        target.textContent = message;
        target.classList.toggle("field-error", type === "error");
        target.classList.toggle("field-success", type === "success");
        target.classList.toggle("field-hint", type === "info");
    };

    const setLoading = (button, loading) => {
        if (!button) return;
        button.disabled = loading;
        button.classList.toggle("is-loading", loading);
        const label = button.querySelector("[data-button-label]");
        if (label) {
            label.textContent = loading ? "Consultando..." : button.dataset.defaultLabel;
        }
    };

    const readError = async (response, fallback) => {
        try {
            const data = await response.json();
            return data.detail || data.message || fallback;
        } catch {
            return fallback;
        }
    };

    const cepInput = document.querySelector("[data-lookup-cep]");
    let ultimoCepConsultado = "";

    const consultarCep = async () => {
        if (!cepInput) return;
        const cep = digits(cepInput.value);
        if (cep.length !== 8) {
            showLookupState(cepInput, "Informe um CEP com 8 dígitos.", "error");
            return;
        }
        if (cep === ultimoCepConsultado) return;

        showLookupState(cepInput, "Consultando CEP...");
        try {
            const response = await fetch(`/integracoes/cep/${cep}`, {
                headers: { Accept: "application/json" },
            });
            if (!response.ok) {
                throw new Error(await readError(response, "CEP não encontrado."));
            }

            const data = await response.json();
            setIfPresent("logradouro", data.logradouro);
            setIfPresent("bairro", data.bairro);
            setIfPresent("cidade", data.cidade);
            setIfPresent("estado", data.estado);
            ultimoCepConsultado = cep;
            showLookupState(cepInput, "CEP localizado e endereço preenchido.", "success");
        } catch (error) {
            showLookupState(cepInput, error.message || "Não foi possível consultar o CEP.", "error");
        }
    };

    cepInput?.addEventListener("blur", consultarCep);

    const documentoInput = document.querySelector("[data-lookup-cnpj]");
    const consultarCnpjButton = document.querySelector("[data-action='consultar-cnpj']");
    let ultimoCnpjConsultado = "";

    const consultarCnpj = async ({ force = false } = {}) => {
        if (!documentoInput) return;
        const cnpj = digits(documentoInput.value);

        if (cnpj.length !== 14) {
            if (force) {
                showLookupState(documentoInput, "Informe um CNPJ com 14 dígitos.", "error");
            }
            return;
        }
        if (!force && cnpj === ultimoCnpjConsultado) return;

        setLoading(consultarCnpjButton, true);
        showLookupState(documentoInput, "Consultando CNPJ...");
        try {
            const response = await fetch(`/integracoes/cnpj/${cnpj}`, {
                headers: { Accept: "application/json" },
            });
            if (!response.ok) {
                throw new Error(await readError(response, "CNPJ não encontrado."));
            }

            const data = await response.json();
            setIfPresent("nome", data.razaoSocial);
            setIfPresent("nomeFantasia", data.nomeFantasia);
            setIfPresent("email", data.email);
            setIfPresent("telefone", data.telefone, maskPhone);
            setIfPresent("cep", data.cep, maskCep);
            setIfPresent("logradouro", data.logradouro);
            setIfPresent("numero", data.numero);
            setIfPresent("complemento", data.complemento);
            setIfPresent("bairro", data.bairro);
            setIfPresent("cidade", data.municipio);
            setIfPresent("estado", data.uf);

            ultimoCnpjConsultado = cnpj;
            const situacao = data.situacaoCadastral ? ` Situação: ${data.situacaoCadastral}.` : "";
            showLookupState(
                documentoInput,
                `CNPJ localizado e dados preenchidos.${situacao}`,
                "success",
            );
        } catch (error) {
            showLookupState(documentoInput, error.message || "Não foi possível consultar o CNPJ.", "error");
        } finally {
            setLoading(consultarCnpjButton, false);
        }
    };

    documentoInput?.addEventListener("blur", () => consultarCnpj());
    consultarCnpjButton?.addEventListener("click", () => consultarCnpj({ force: true }));
})();
