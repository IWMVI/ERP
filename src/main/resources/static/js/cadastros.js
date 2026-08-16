(() => {
    const digits = (value) => (value || "").replace(/\D/g, "");
    const documentChars = (value) =>
        (value || "").toUpperCase().replace(/[^A-Z0-9]/g, "").slice(0, 14);

    const maskCpf = (value) =>
        digits(value)
            .slice(0, 11)
            .replace(/(\d{3})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d{1,2})$/, "$1-$2");

    const maskCpfCnpj = (value) => {
        const raw = documentChars(value);
        const isCnpj = /[A-Z]/.test(raw) || raw.length > 11;

        if (!isCnpj) {
            return maskCpf(raw);
        }

        return raw
            .replace(/([A-Z0-9]{2})([A-Z0-9])/, "$1.$2")
            .replace(/([A-Z0-9]{3})([A-Z0-9])/, "$1.$2")
            .replace(/([A-Z0-9]{3})([A-Z0-9])/, "$1/$2")
            .replace(/([A-Z0-9]{4})(\d{1,2})$/, "$1-$2");
    };

    const maskCep = (value) => digits(value).slice(0, 8).replace(/(\d{5})(\d)/, "$1-$2");

    const maskPhone = (value) => {
        const raw = digits(value).slice(0, 11);
        if (raw.length <= 10) {
            return raw.replace(/(\d{2})(\d)/, "($1) $2").replace(/(\d{4})(\d)/, "$1-$2");
        }
        return raw.replace(/(\d{2})(\d)/, "($1) $2").replace(/(\d{5})(\d)/, "$1-$2");
    };

    const maskGtin = (value) => digits(value).slice(0, 14);

    const formatters = {
        cpf: maskCpf,
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
        if (formatter) element.textContent = formatter(element.textContent ?? "");
    });

    document.querySelectorAll("[data-image-input]").forEach((input) => {
        input.addEventListener("change", () => {
            const file = input.files?.[0];
            if (!file) return;

            const panel = input.closest(".entity-photo-panel");
            const previewContainer = panel?.querySelector(".entity-photo-preview");
            if (!previewContainer) return;

            const oldPreview = previewContainer.querySelector("[data-image-preview]");
            const placeholder = previewContainer.querySelector("[data-image-placeholder]");
            const image = oldPreview || document.createElement("img");
            image.dataset.imagePreview = "";
            image.alt = "Pré-visualização da imagem";
            image.src = URL.createObjectURL(file);

            if (!oldPreview) previewContainer.appendChild(image);
            if (placeholder) placeholder.hidden = true;
        });
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
        const label = button.querySelector("[data-button-label]");
        if (label) label.textContent = loading ? "Consultando..." : button.dataset.defaultLabel;
    };

    const readError = async (response, fallback) => {
        try {
            const data = await response.json();
            return data.detail || data.message || fallback;
        } catch {
            return fallback;
        }
    };

    const documentoInput = document.querySelector("[data-documento-pessoa]");
    const documentoTipo = document.querySelector("[data-documento-tipo]");
    const tipoPessoaInput = document.querySelector("[name='tipoPessoa']");
    const consultarCnpjButton = document.querySelector("[data-action='consultar-cnpj']");
    const nomeLabel = document.querySelector("[data-label-nome-pessoa]");

    const atualizarCamposPorTipo = (tipo) => {
        document.querySelectorAll("[data-pessoa-juridica]").forEach((element) => {
            element.hidden = tipo !== "JURIDICA";
        });
        document.querySelectorAll("[data-pessoa-fisica]").forEach((element) => {
            element.hidden = tipo !== "FISICA";
        });
        if (nomeLabel) {
            nomeLabel.textContent =
                tipo === "JURIDICA"
                    ? "Razão social"
                    : tipo === "FISICA"
                      ? "Nome completo"
                      : "Nome / Razão social";
        }
    };

    const atualizarTipoDocumento = () => {
        if (!documentoInput) return;
        const raw = documentChars(documentoInput.value);
        const cpf = /^\d{11}$/.test(raw);
        const cnpj = /^[A-Z0-9]{12}\d{2}$/.test(raw);
        const tipo = cpf ? "FISICA" : cnpj ? "JURIDICA" : "";

        if (tipoPessoaInput) tipoPessoaInput.value = tipo;
        if (documentoTipo) {
            documentoTipo.textContent = cpf
                ? "CPF · Pessoa física"
                : cnpj
                  ? "CNPJ · Pessoa jurídica"
                  : "Informe CPF ou CNPJ";
        }
        if (consultarCnpjButton) consultarCnpjButton.hidden = !cnpj;
        atualizarCamposPorTipo(tipo);
    };

    documentoInput?.addEventListener("input", atualizarTipoDocumento);
    atualizarTipoDocumento();

    const cepInput = document.querySelector("[data-lookup-cep]");
    let ultimoCepConsultado = "";

    const consultarCep = async () => {
        if (!cepInput) return;
        const cep = digits(cepInput.value);
        if (!cep) return;
        if (cep.length !== 8) {
            showLookupState(cepInput, "CEP deve possuir 8 dígitos.", "error");
            return;
        }
        if (cep === ultimoCepConsultado) return;

        showLookupState(cepInput, "Consultando CEP...");
        try {
            const response = await fetch(`/integracoes/cep/${cep}`, {
                headers: { Accept: "application/json" },
            });
            if (!response.ok) throw new Error(await readError(response, "CEP não encontrado."));
            const data = await response.json();
            setIfPresent("logradouro", data.logradouro);
            setIfPresent("bairro", data.bairro);
            setIfPresent("cidade", data.cidade);
            setIfPresent("estado", data.estado);
            ultimoCepConsultado = cep;
            showLookupState(cepInput, "Endereço preenchido a partir do CEP.", "success");
        } catch (error) {
            showLookupState(cepInput, error.message || "Não foi possível consultar o CEP.", "error");
        }
    };

    cepInput?.addEventListener("blur", consultarCep);

    let ultimoCnpjConsultado = "";
    const consultarCnpj = async ({ force = false } = {}) => {
        if (!documentoInput) return;
        const cnpj = documentChars(documentoInput.value);
        if (!/^[A-Z0-9]{12}\d{2}$/.test(cnpj)) {
            if (force) {
                showLookupState(documentoInput, "Informe um CNPJ válido com 14 posições.", "error");
            }
            return;
        }
        if (!force && cnpj === ultimoCnpjConsultado) return;

        setLoading(consultarCnpjButton, true);
        showLookupState(documentoInput, "Consultando dados do CNPJ...");
        try {
            const response = await fetch(`/integracoes/cnpj/${cnpj}`, {
                headers: { Accept: "application/json" },
            });
            if (!response.ok) throw new Error(await readError(response, "CNPJ não encontrado."));
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
            showLookupState(documentoInput, `Dados do CNPJ carregados.${situacao}`, "success");
        } catch (error) {
            showLookupState(documentoInput, error.message || "Não foi possível consultar o CNPJ.", "error");
        } finally {
            setLoading(consultarCnpjButton, false);
        }
    };

    documentoInput?.addEventListener("blur", () => consultarCnpj());
    consultarCnpjButton?.addEventListener("click", () => consultarCnpj({ force: true }));
})();
