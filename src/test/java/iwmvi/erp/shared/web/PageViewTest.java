package iwmvi.erp.shared.web;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PageViewTest {

    private static final String BASE_PATH = "/produtos";

    private List<Integer> itens(int quantidade) {
        return IntStream.rangeClosed(1, quantidade)
            .boxed()
            .toList();
    }

    private List<Integer> itens(int de, int ate) {
        return IntStream.rangeClosed(de, ate)
            .boxed()
            .toList();
    }

    private PageView<Integer> pagina(int quantidade, int numeroPagina) {
        return PageView.of(
            itens(quantidade),
            numeroPagina,
            BASE_PATH);
    }

    @Test
    @DisplayName("deve criar a primeira página com até 10 itens")
    void deveCriarPrimeiraPagina() {
        PageView<Integer> pagina = pagina(5, 0);

        assertEquals(itens(5), pagina.items());
        assertEquals(0, pagina.page());
        assertEquals(1, pagina.displayPage());
        assertEquals(1, pagina.totalPages());
        assertEquals(5, pagina.totalItems());
    }

    @Test
    @DisplayName("deve dividir os itens em páginas de 10 itens")
    void deveDividirItensEmPaginas() {
        PageView<Integer> pagina = pagina(25, 1);

        assertEquals(itens(11, 20), pagina.items());
        assertEquals(1, pagina.page());
        assertEquals(2, pagina.displayPage());
        assertEquals(3, pagina.totalPages());
        assertEquals(25, pagina.totalItems());
    }

    @Test
    @DisplayName("deve retornar os itens restantes na última página")
    void deveRetornarItensRestantesNaUltimaPagina() {
        PageView<Integer> pagina = pagina(25, 2);

        assertEquals(itens(21, 25), pagina.items());
        assertEquals(2, pagina.page());
        assertEquals(3, pagina.displayPage());
    }

    @Test
    @DisplayName("deve limitar página negativa para a primeira página")
    void deveLimitarPaginaNegativa() {
        PageView<Integer> pagina = pagina(25, -10);

        assertEquals(0, pagina.page());
        assertEquals(1, pagina.displayPage());
        assertEquals(itens(1, 10), pagina.items());
    }

    @Test
    @DisplayName("deve limitar página maior que o total para a última página")
    void deveLimitarPaginaMaiorQueTotal() {
        PageView<Integer> pagina = pagina(25, 100);

        assertEquals(2, pagina.page());
        assertEquals(3, pagina.displayPage());
        assertEquals(itens(21, 25), pagina.items());
    }

    @Test
    @DisplayName("deve criar uma página mesmo quando a lista estiver vazia")
    void deveCriarPaginaParaListaVazia() {
        PageView<Integer> pagina = pagina(0, 0);

        assertTrue(pagina.items().isEmpty());
        assertEquals(0, pagina.page());
        assertEquals(1, pagina.displayPage());
        assertEquals(1, pagina.totalPages());
        assertEquals(0, pagina.totalItems());
        assertFalse(pagina.hasPrevious());
        assertFalse(pagina.hasNext());
    }

    @Test
    @DisplayName("deve considerar exatamente 10 itens como uma única página")
    void deveConsiderarDezItensComoUmaPagina() {
        PageView<Integer> pagina = pagina(10, 0);

        assertEquals(1, pagina.totalPages());
        assertEquals(10, pagina.totalItems());
        assertEquals(10, pagina.items().size());
        assertFalse(pagina.hasPrevious());
        assertFalse(pagina.hasNext());
    }

    @Test
    @DisplayName("deve criar segunda página quando houver 11 itens")
    void deveCriarSegundaPaginaComOnzeItens() {
        PageView<Integer> pagina = pagina(11, 1);

        assertEquals(2, pagina.totalPages());
        assertEquals(11, pagina.totalItems());
        assertEquals(List.of(11), pagina.items());
        assertEquals(1, pagina.page());
    }

    @Test
    @DisplayName("deve informar corretamente se existe página anterior")
    void deveInformarSeExistePaginaAnterior() {
        PageView<Integer> primeira = pagina(25, 0);
        PageView<Integer> segunda = pagina(25, 1);

        assertFalse(primeira.hasPrevious());
        assertTrue(segunda.hasPrevious());
    }

    @Test
    @DisplayName("deve informar corretamente se existe próxima página")
    void deveInformarSeExisteProximaPagina() {
        PageView<Integer> primeira = pagina(25, 0);
        PageView<Integer> ultima = pagina(25, 2);

        assertTrue(primeira.hasNext());
        assertFalse(ultima.hasNext());
    }

    @Test
    @DisplayName("deve gerar URL da próxima página")
    void deveGerarUrlDaProximaPagina() {
        PageView<Integer> pagina = pagina(25, 0);

        assertEquals(
            "/produtos?page=1",
            pagina.nextUrl());
    }

    @Test
    @DisplayName("deve gerar URL da página anterior")
    void deveGerarUrlDaPaginaAnterior() {
        PageView<Integer> pagina = pagina(25, 1);

        assertEquals(
            "/produtos?page=0",
            pagina.previousUrl());
    }

    @Test
    @DisplayName("deve limitar página negativa ao gerar URL")
    void deveLimitarPaginaNegativaNaUrl() {
        PageView<Integer> pagina = pagina(3, 0);

        assertEquals(
            "/produtos?page=0",
            pagina.url(-100));
    }

    @Test
    @DisplayName("deve limitar página maior que o total ao gerar URL")
    void deveLimitarPaginaMaiorQueTotalNaUrl() {
        PageView<Integer> pagina = pagina(3, 0);

        assertEquals(
            "/produtos?page=0",
            pagina.url(100));
    }

    @Test
    @DisplayName("deve incluir parâmetro com valor preenchido na URL")
    void deveIncluirParametroComValorPreenchidoNaUrl() {
        PageView<Integer> pagina =
            PageView.of(
                itens(3),
                0,
                BASE_PATH,
                Map.of("status", "ativo"));

        assertEquals(
            "/produtos?page=0&status=ativo",
            pagina.url(0));
    }

    @Test
    @DisplayName("deve ignorar parâmetro com valor nulo")
    void deveIgnorarParametroComValorNulo() {
        Map<String, Object> parametros = new LinkedHashMap<>();
        parametros.put("status", null);

        PageView<Integer> pagina =
            PageView.of(
                itens(3),
                0,
                BASE_PATH,
                parametros);

        assertEquals(
            "/produtos?page=0",
            pagina.url(0));
    }

    @Test
    @DisplayName("deve ignorar parâmetro com valor vazio")
    void deveIgnorarParametroComValorVazio() {
        PageView<Integer> pagina =
            PageView.of(
                itens(3),
                0,
                BASE_PATH,
                Map.of("status", ""));

        assertEquals(
            "/produtos?page=0",
            pagina.url(0));
    }

    @Test
    @DisplayName("deve ignorar parâmetro com valor em branco")
    void deveIgnorarParametroComValorEmBranco() {
        PageView<Integer> pagina =
            PageView.of(
                itens(3),
                0,
                BASE_PATH,
                Map.of("status", "   "));

        assertEquals(
            "/produtos?page=0",
            pagina.url(0));
    }

    @Test
    @DisplayName("deve ignorar parâmetro contendo apenas espaços")
    void deveIgnorarParametroContendoApenasEspacos() {
        PageView<Integer> pagina =
            PageView.of(
                itens(3),
                0,
                BASE_PATH,
                Map.of("status", "     "));

        assertEquals(
            "/produtos?page=0",
            pagina.url(0));
    }

    @Test
    @DisplayName("deve incluir múltiplos parâmetros na URL")
    void deveIncluirMultiplosParametrosNaUrl() {
        PageView<Integer> pagina =
            PageView.of(
                itens(3),
                0,
                BASE_PATH,
                Map.of(
                    "nome", "João",
                    "status", "ativo"));

        assertEquals(
            "/produtos?page=0&nome=Jo%C3%A3o&status=ativo",
            pagina.url(0));
    }

    @Test
    @DisplayName("deve codificar chave e valor dos parâmetros")
    void deveCodificarParametrosDaUrl() {
        PageView<Integer> pagina =
            PageView.of(
                itens(3),
                0,
                BASE_PATH,
                Map.of(
                    "nome do campo",
                    "João & Maria"));

        assertEquals(
            "/produtos?page=0&nome+do+campo=Jo%C3%A3o+%26+Maria",
            pagina.url(0));
    }

    @Test
    @DisplayName("deve utilizar parâmetro de página personalizado")
    void deveUtilizarParametroDePaginaPersonalizado() {
        PageView<Integer> pagina =
            PageView.of(
                itens(3),
                0,
                BASE_PATH,
                "pagina",
                Map.of());

        assertEquals(
            "/produtos?pagina=0",
            pagina.url(0));
    }

    @Test
    @DisplayName("deve manter parâmetros na URL da próxima página")
    void deveManterParametrosNaUrlDaProximaPagina() {
        PageView<Integer> pagina =
            PageView.of(
                itens(25),
                0,
                BASE_PATH,
                Map.of("status", "ativo"));

        assertEquals(
            "/produtos?page=1&status=ativo",
            pagina.nextUrl());
    }

    @Test
    @DisplayName("deve manter parâmetros na URL da página anterior")
    void deveManterParametrosNaUrlDaPaginaAnterior() {
        PageView<Integer> pagina =
            PageView.of(
                itens(25),
                1,
                BASE_PATH,
                Map.of("status", "ativo"));

        assertEquals(
            "/produtos?page=0&status=ativo",
            pagina.previousUrl());
    }

    @Test
    @DisplayName("deve retornar uma lista imutável")
    void deveRetornarListaImutavel() {
        PageView<Integer> pagina = pagina(3, 0);

        assertThrows(
            UnsupportedOperationException.class,
            () -> pagina.items().add(4));
    }
}
