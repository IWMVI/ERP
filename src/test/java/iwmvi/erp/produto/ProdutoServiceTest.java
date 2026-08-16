package iwmvi.erp.produto;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.integracao.ValidacaoCadastroService;
import iwmvi.erp.shared.exception.CodigoProdutoJaCadastradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;
    @Mock
    private AuditoriaService auditoriaService;
    @Mock
    private ValidacaoCadastroService validacaoCadastroService;
    private ProdutoService service;

    @BeforeEach
    void setUp() {
        service = new ProdutoService(repository, auditoriaService, validacaoCadastroService);
    }

    @Test
    void deveRejeitarCodigoDuplicado() {
        ProdutoRequest request =
            new ProdutoRequest(
                "SKU-1",
                "Produto",
                "Categoria",
                UnidadeMedida.UNIDADE,
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.ZERO);
        when(repository.existsByCodigo("SKU-1")).thenReturn(true);

        assertThrows(CodigoProdutoJaCadastradoException.class, () -> service.criar(request));

        verify(validacaoCadastroService, never()).validarGtin(any());
        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarEstoqueMaximoMenorQueMinimo() {
        ProdutoRequest request =
            new ProdutoRequest(
                "SKU-2",
                "",
                "Produto",
                "",
                "",
                "Categoria",
                "",
                UnidadeMedida.UNIDADE,
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.TEN,
                BigDecimal.ONE,
                "");

        assertThrows(IllegalArgumentException.class, () -> service.criar(request));
        verify(repository, never()).save(any());
    }
}
