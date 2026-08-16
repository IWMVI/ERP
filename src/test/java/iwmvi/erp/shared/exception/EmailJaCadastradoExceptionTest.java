package iwmvi.erp.shared.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmailJaCadastradoExceptionTest {

    @Test
    void deveInstanciarExcecao() {
        EmailJaCadastradoException exception = new EmailJaCadastradoException("wallace@gmail.com");

        assertNotNull(exception);
        assertEquals("O e-mail wallace@gmail.com já está cadastrado.", exception.getMessage());
    }
}
