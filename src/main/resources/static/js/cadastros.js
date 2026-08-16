(() => {
    const digits = (value) => value.replace(/\D/g, "");

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

    const setIfPresent = (name, value) => {
        if (!value) return;
        const input = document.querySelector(`[name="${name}"]`);
        if (!input) return;
        input.value = value;
        const formatter = formatters[input.dataset.mask];
        if (formatter) input.value = formatter(input.value);
    };

    const showLookupState = (input, message, error = false) => {
        const target = input.closest(".field-group")?.querySelector("[data-lookup-status]");
        if (!target) return;
        target.textContent = message;
        target.classList.toggle("field-error", error);
        target.classList.toggle("field-hint", !error);
    };

    const cepInput = document.querySelector("[data-lookup-cep]");
    cepInput?.addEventListener("blur", async () => {
        const cep = digits(cepInput.value);
        if (cep.length !== 8) return;

        showLookupState(cepInput, "Consultando CEP...");
        try {
            const response = await fetch(`/integracoes/cep/${cep}`);
            if (!response.ok) throw new Error();
            const data = await response.json();
            setIfPresent("logradouro", data.street);
            setIfPresent("bairro", data.neighborhood);
            setIfPresent("cidade", data.city);
            setIfPresent("estado", data.state);
            showLookupState(cepInput, "Endereço localizado.");
        } catch {
            showLookupState(cepInput, "Não foi possível localizar o CEP.", true);
        }
    });

    const documentoInput = document.querySelector("[data-lookup-cnpj]");
    documentoInput?.addEventListener("blur", async () => {
        const cnpj = digits(documentoInput.value);
        if (cnpj.length !== 14) return;

        showLookupState(documentoInput, "Consultando CNPJ...");
        try {
            const response = await fetch(`/integracoes/cnpj/${cnpj}`);
            if (!response.ok) throw new Error();
            const data = await response.json();
            setIfPresent("nome", data.razaoSocial);
            setIfPresent("nomeFantasia", data.nomeFantasia);
            setIfPresent("email", data.email);
            setIfPresent("telefone", data.telefone);
            setIfPresent("cep", data.cep);
            setIfPresent("logradouro", data.logradouro);
            setIfPresent("numero", data.numero);
            setIfPresent("complemento", data.complemento);
            setIfPresent("bairro", data.bairro);
            setIfPresent("cidade", data.municipio);
            setIfPresent("estado", data.uf);
            showLookupState(documentoInput, `CNPJ localizado${data.situacaoCadastral ? ` · ${data.situacaoCadastral}` : ""}.`);
        } catch {
            showLookupState(documentoInput, "Não foi possível consultar o CNPJ.", true);
        }
    });
})();
