package iwmvi.erp.fornecedor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FornecedorServiceTest {

    @Mock private FornecedorRepository repository;
    @Mock private AuditoriaService auditoriaService;
    private FornecedorService service;

    @BeforeEach
    void setUp() {
        service = new FornecedorService(repository, auditoriaService);
    }

    @Test
    void deveRejeitarDocumentoDuplicado() {
        FornecedorRequest request =
                new FornecedorRequest(
                        "Fornecedor",
                        "12345678000199",
                        "fornecedor@teste.com",
                        "",
                        "",
                        "",
                        "",
                        "SP",
                        "");
        when(repository.existsByDocumento("12345678000199")).thenReturn(true);

        assertThrows(DocumentoJaCadastradoException.class, () -> service.criar(request));

        verify(repository, never()).save(any());
    }
}
