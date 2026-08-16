package iwmvi.erp.integracao;

import iwmvi.erp.shared.validation.DocumentoValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/integracoes")
public class IntegracaoCadastroController {

    private final BrasilApiClient brasilApiClient;

    public IntegracaoCadastroController(BrasilApiClient brasilApiClient) {
        this.brasilApiClient = brasilApiClient;
    }

    @GetMapping("/cep/{cep}")
    public BrasilApiClient.CepDados cep(@PathVariable String cep) {
        String digits = DocumentoValidator.somenteDigitos(cep);
        if (digits.length() != 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido.");
        }
        return brasilApiClient.consultarCep(digits);
    }

    @GetMapping("/cnpj/{cnpj}")
    public BrasilApiClient.CnpjDados cnpj(@PathVariable String cnpj) {
        String digits = DocumentoValidator.somenteDigitos(cnpj);
        if (!DocumentoValidator.cnpjValido(digits)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ inválido.");
        }
        return brasilApiClient.consultarCnpj(digits);
    }
}
