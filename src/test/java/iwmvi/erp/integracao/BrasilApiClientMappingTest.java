package iwmvi.erp.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

class BrasilApiClientMappingTest {

    private final JsonMapper objectMapper = JsonMapper.builder().build();

    @Test
    void deveMapearDadosPrincipaisDoCnpj() throws Exception {
        String json = """
                {
                  "cnpj": "19131243000197",
                  "razao_social": "OPEN KNOWLEDGE BRASIL",
                  "nome_fantasia": "REDE PELO CONHECIMENTO LIVRE",
                  "descricao_situacao_cadastral": "ATIVA",
                  "descricao_tipo_de_logradouro": "AVENIDA",
                  "logradouro": "PAULISTA 37",
                  "numero": "37",
                  "complemento": "ANDAR 4",
                  "bairro": "BELA VISTA",
                  "cep": "01311902",
                  "uf": "SP",
                  "municipio": "SAO PAULO",
                  "email": null,
                  "ddd_telefone_1": "1123851939"
                }
                """;

        BrasilApiClient.CnpjDados dados = objectMapper.readValue(json, BrasilApiClient.CnpjDados.class);

        assertEquals("19131243000197", dados.cnpj());
        assertEquals("OPEN KNOWLEDGE BRASIL", dados.razaoSocial());
        assertEquals("REDE PELO CONHECIMENTO LIVRE", dados.nomeFantasia());
        assertEquals("ATIVA", dados.situacaoCadastral());
        assertEquals("AVENIDA", dados.tipoLogradouro());
        assertEquals("PAULISTA 37", dados.logradouro());
        assertEquals("SAO PAULO", dados.municipio());
        assertEquals("1123851939", dados.telefone());
    }
}
