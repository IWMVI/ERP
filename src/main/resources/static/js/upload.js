(() => {
    document.querySelectorAll("[data-image-field]").forEach((field) => {
        const input = field.querySelector("[data-image-input]");
        const fileName = field.querySelector("[data-image-file-name]");
        const previewContainer = field.querySelector(".registry-image-preview");
        const placeholder = field.querySelector("[data-image-placeholder]");
        const clearButton = field.querySelector("[data-image-clear]");

        if (!input || !previewContainer) return;

        let previewUrl = null;

        const clearPreviewUrl = () => {
            if (previewUrl) {
                URL.revokeObjectURL(previewUrl);
                previewUrl = null;
            }
        };

        input.addEventListener("change", () => {
            const file = input.files?.[0];
            if (!file) return;

            const allowedTypes = new Set(["image/jpeg", "image/png"]);
            if (!allowedTypes.has(file.type) || file.size > 5 * 1024 * 1024) {
                input.value = "";
                if (fileName) fileName.textContent = "Arquivo inválido. Use JPG ou PNG de até 5 MB.";
                if (clearButton) clearButton.hidden = true;
                return;
            }

            clearPreviewUrl();
            previewUrl = URL.createObjectURL(file);

            let image = previewContainer.querySelector("[data-image-preview]");
            if (!image) {
                image = document.createElement("img");
                image.dataset.imagePreview = "";
                image.alt = "Pré-visualização da imagem";
                previewContainer.appendChild(image);
            }

            image.src = previewUrl;
            if (placeholder) placeholder.hidden = true;
            if (fileName) fileName.textContent = file.name;
            if (clearButton) clearButton.hidden = false;
        });

        clearButton?.addEventListener("click", () => {
            input.value = "";
            clearPreviewUrl();
            const image = previewContainer.querySelector("[data-image-preview]");
            if (image && !image.hasAttribute("src")) image.remove();
            if (fileName) fileName.textContent = "Nenhum arquivo selecionado";
            if (placeholder) placeholder.hidden = false;
            clearButton.hidden = true;
        });

        window.addEventListener("beforeunload", clearPreviewUrl, { once: true });
    });
})();
