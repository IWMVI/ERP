package iwmvi.erp.integracao;

import iwmvi.erp.shared.exception.CepInvalidoException;
import iwmvi.erp.shared.exception.DocumentoInvalidoException;
import iwmvi.erp.shared.validation.DocumentoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Service
public class ValidacaoCadastroService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidacaoCadastroService.class);

    private final BrasilApiClient brasilApiClient;

    public ValidacaoCadastroService(BrasilApiClient brasilApiClient) {
        this.brasilApiClient = brasilApiClient;
    }

    public void validarDocumento(String documento) {
        String normalizado = DocumentoValidator.normalizarDocumento(documento);

        if (DocumentoValidator.documentoEhCpf(normalizado)) {
            if (!DocumentoValidator.cpfValido(normalizado)) {
                throw new DocumentoInvalidoException("CPF inválido.");
            }
            return;
        }

        if (DocumentoValidator.documentoEhCnpj(normalizado)) {
            if (!DocumentoValidator.cnpjValido(normalizado)) {
                throw new DocumentoInvalidoException("CNPJ inválido.");
            }
            validarExistenciaCnpj(normalizado);
            return;
        }

        throw new DocumentoInvalidoException(
            "Informe um CPF com 11 dígitos ou um CNPJ válido com 14 posições.");
    }

    public void validarCep(String cep) {
        if (cep == null || cep.isBlank()) {
            return;
        }

        String digits = DocumentoValidator.somenteDigitos(cep);
        if (digits.length() != 8) {
            throw new CepInvalidoException("CEP inválido. Informe 8 dígitos.");
        }

        try {
            brasilApiClient.consultarCep(digits);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new CepInvalidoException("CEP não encontrado.");
        } catch (RestClientException exception) {
            LOGGER.warn("Não foi possível validar o CEP {} na BrasilAPI.", digits, exception);
        }
    }

    public void validarGtin(String gtin) {
        if (gtin == null || gtin.isBlank()) {
            return;
        }

        if (!DocumentoValidator.gtinValido(gtin)) {
            throw new DocumentoInvalidoException("Código de barras GTIN/EAN inválido.");
        }
    }

    private void validarExistenciaCnpj(String cnpj) {
        try {
            brasilApiClient.consultarCnpj(cnpj);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new DocumentoInvalidoException("CNPJ não encontrado na base pública consultada.");
        } catch (RestClientException exception) {
            LOGGER.warn("Não foi possível validar o CNPJ {} na BrasilAPI.", cnpj, exception);
        }
    }
}
