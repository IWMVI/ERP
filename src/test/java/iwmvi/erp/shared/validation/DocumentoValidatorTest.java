package iwmvi.erp.shared.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DocumentoValidatorTest {

    @Test
    void deveValidarCpf() {
        assertTrue(DocumentoValidator.cpfValido("529.982.247-25"));
        assertFalse(DocumentoValidator.cpfValido("529.982.247-24"));
        assertFalse(DocumentoValidator.cpfValido("111.111.111-11"));
    }

    @Test
    void deveValidarCnpj() {
        assertTrue(DocumentoValidator.cnpjValido("11.222.333/0001-81"));
        assertFalse(DocumentoValidator.cnpjValido("11.222.333/0001-82"));
        assertFalse(DocumentoValidator.cnpjValido("11.111.111/1111-11"));
    }

    @Test
    void deveValidarGtin() {
        assertTrue(DocumentoValidator.gtinValido("7894900011517"));
        assertFalse(DocumentoValidator.gtinValido("7894900011518"));
    }
}
