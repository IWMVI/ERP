package iwmvi.erp.integracao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BrasilApiClient {

    private final RestClient restClient;

    public BrasilApiClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://brasilapi.com.br/api").build();
    }

    public CepDados consultarCep(String cep) {
        return restClient.get().uri("/cep/v2/{cep}", cep).retrieve().body(CepDados.class);
    }

    public CnpjDados consultarCnpj(String cnpj) {
        return restClient.get().uri("/cnpj/v1/{cnpj}", cnpj).retrieve().body(CnpjDados.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CepDados(
        String cep, String state, String city, String neighborhood, String street, String service) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CnpjDados(
        String cnpj,
        @JsonProperty("razao_social") String razaoSocial,
        @JsonProperty("nome_fantasia") String nomeFantasia,
        @JsonProperty("descricao_situacao_cadastral") String situacaoCadastral,
        @JsonProperty("descricao_tipo_de_logradouro") String tipoLogradouro,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cep,
        String uf,
        String municipio,
        String email,
        @JsonProperty("ddd_telefone_1") String telefone) {
    }
}
