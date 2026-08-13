package iwmvi.erp.shared.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class EmailJaCadastradoExceptionTest {

    @Test
    void deveInstanciarExcecao() {
        EmailJaCadastradoException exception = new EmailJaCadastradoException("wallace@gmail.com");

        assertNotNull(exception);
        assertEquals("O e-mail wallace@gmail.com já está cadastrado.", exception.getMessage());
    }
}
