package iwmvi.erp.produto;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.shared.exception.CodigoProdutoJaCadastradoException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock private ProdutoRepository repository;
    @Mock private AuditoriaService auditoriaService;
    private ProdutoService service;

    @BeforeEach
    void setUp() {
        service = new ProdutoService(repository, auditoriaService);
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

        verify(repository, never()).save(any());
    }
}
