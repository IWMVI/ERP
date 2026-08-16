package iwmvi.erp.cliente;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import iwmvi.erp.auditoria.AuditoriaService;
import iwmvi.erp.integracao.ValidacaoCadastroService;
import iwmvi.erp.shared.exception.DocumentoJaCadastradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock private ClienteRepository repository;
    @Mock private AuditoriaService auditoriaService;
    @Mock private ValidacaoCadastroService validacaoCadastroService;
    private ClienteService service;

    @BeforeEach
    void setUp() {
        service = new ClienteService(repository, auditoriaService, validacaoCadastroService);
    }

    @Test
    void deveRejeitarDocumentoDuplicado() {
        ClienteRequest request =
                new ClienteRequest(
                        TipoPessoa.FISICA,
                        "Cliente",
                        "12345678900",
                        "cliente@teste.com",
                        "",
                        "",
                        "",
                        "",
                        "SP",
                        "");
        when(repository.existsByDocumento("12345678900")).thenReturn(true);

        assertThrows(DocumentoJaCadastradoException.class, () -> service.criar(request));

        verify(validacaoCadastroService, never()).validarDocumento(any());
        verify(repository, never()).save(any());
    }
}
