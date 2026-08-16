package iwmvi.erp.integracao;

import iwmvi.erp.shared.validation.DocumentoValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/integracoes")
public class IntegracaoCadastroController {

    private final BrasilApiClient brasilApiClient;

    public IntegracaoCadastroController(BrasilApiClient brasilApiClient) {
        this.brasilApiClient = brasilApiClient;
    }

    @GetMapping("/cep/{cep}")
    public CepResponse cep(@PathVariable String cep) {
        String digits = DocumentoValidator.somenteDigitos(cep);
        if (digits.length() != 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido.");
        }

        try {
            BrasilApiClient.CepDados dados = brasilApiClient.consultarCep(digits);
            if (dados == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado.");
            }
            return new CepResponse(
                    dados.cep(),
                    dados.state(),
                    dados.city(),
                    dados.neighborhood(),
                    dados.street());
        } catch (RestClientResponseException exception) {
            throw traduzirErroExterno(exception, "CEP");
        } catch (RestClientException exception) {
            throw servicoIndisponivel("CEP");
        }
    }

    @GetMapping("/cnpj/{cnpj}")
    public CnpjResponse cnpj(@PathVariable String cnpj) {
        String digits = DocumentoValidator.somenteDigitos(cnpj);
        if (!DocumentoValidator.cnpjValido(digits)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ inválido.");
        }

        try {
            BrasilApiClient.CnpjDados dados = brasilApiClient.consultarCnpj(digits);
            if (dados == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CNPJ não encontrado.");
            }

            return new CnpjResponse(
                    dados.cnpj(),
                    dados.razaoSocial(),
                    dados.nomeFantasia(),
                    dados.situacaoCadastral(),
                    montarLogradouro(dados),
                    dados.numero(),
                    dados.complemento(),
                    dados.bairro(),
                    dados.cep(),
                    dados.uf(),
                    dados.municipio(),
                    dados.email(),
                    dados.telefone());
        } catch (RestClientResponseException exception) {
            throw traduzirErroExterno(exception, "CNPJ");
        } catch (RestClientException exception) {
            throw servicoIndisponivel("CNPJ");
        }
    }

    private ResponseStatusException traduzirErroExterno(
            RestClientResponseException exception, String tipo) {
        if (exception.getStatusCode().value() == 400) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, tipo + " inválido.");
        }
        if (exception.getStatusCode().value() == 404) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, tipo + " não encontrado.");
        }
        return servicoIndisponivel(tipo);
    }

    private ResponseStatusException servicoIndisponivel(String tipo) {
        return new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "Não foi possível consultar " + tipo + " no serviço externo.");
    }

    private String montarLogradouro(BrasilApiClient.CnpjDados dados) {
        String tipo = dados.tipoLogradouro();
        String logradouro = dados.logradouro();
        if (tipo == null || tipo.isBlank()) {
            return logradouro;
        }
        if (logradouro == null || logradouro.isBlank()) {
            return tipo;
        }
        return tipo + " " + logradouro;
    }

    public record CepResponse(
            String cep, String estado, String cidade, String bairro, String logradouro) {}

    public record CnpjResponse(
            String cnpj,
            String razaoSocial,
            String nomeFantasia,
            String situacaoCadastral,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cep,
            String uf,
            String municipio,
            String email,
            String telefone) {}
}
