package iwmvi.erp.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class DropdownCoverageTest {

    private static final Path TEMPLATES = Path.of("src/main/resources/templates");

    @Test
    void todoTemplateComSelectDeveCarregarOComponenteGlobal() throws IOException {
        List<Path> templatesComSelect;
        try (var arquivos = Files.walk(TEMPLATES)) {
            templatesComSelect = arquivos
                    .filter(path -> path.toString().endsWith(".html"))
                    .filter(path -> conteudo(path).contains("<select"))
                    .toList();
        }

        assertFalse(templatesComSelect.isEmpty(), "Nenhum select foi encontrado para validar.");
        for (Path template : templatesComSelect) {
            String html = conteudo(template);
            assertTrue(
                    html.contains("fragments/topbar") || html.contains("/js/dropdowns.js"),
                    () -> template + " não carrega o componente de dropdown.");
            assertTrue(
                    html.contains("@{/css/app.css}"),
                    () -> template + " não carrega os estilos globais do dropdown.");
            assertFalse(
                    html.contains("data-native-select=\"true\""),
                    () -> template + " mantém um select nativo fora do padrão global.");
        }
    }

    @Test
    void topbarDeveCarregarScriptQueAprimoraTodosOsSelects() throws IOException {
        String topbar = conteudo(TEMPLATES.resolve("fragments/topbar.html"));
        String script = conteudo(Path.of("src/main/resources/static/js/dropdowns.js"));
        String css = conteudo(Path.of("src/main/resources/static/css/app.css"));

        assertTrue(topbar.contains("@{/js/dropdowns.js}"));
        assertTrue(script.contains("querySelectorAll?.(\"select\")"));
        assertTrue(css.contains(".custom-select-trigger"));
        assertTrue(css.contains(".custom-select-options"));
    }

    private static String conteudo(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível ler " + path, exception);
        }
    }
}
