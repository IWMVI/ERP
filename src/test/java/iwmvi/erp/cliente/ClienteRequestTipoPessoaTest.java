package iwmvi.erp.cliente;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClienteRequestTipoPessoaTest {

    @Test
    void deveIdentificarPessoaFisicaPeloCpf() {
        ClienteRequest request = request("529.982.247-25");

        assertEquals(TipoPessoa.FISICA, request.tipoPessoa());
    }

    @Test
    void deveIdentificarPessoaJuridicaPeloCnpj() {
        ClienteRequest request = request("19.131.243/0001-97");

        assertEquals(TipoPessoa.JURIDICA, request.tipoPessoa());
    }

    @Test
    void naoDeveDefinirTipoParaDocumentoIncompleto() {
        ClienteRequest request = request("12345");

        assertNull(request.tipoPessoa());
    }

    private ClienteRequest request(String documento) {
        return new ClienteRequest(
            null, "Teste", "", documento, "", "", "", "", "", "", "", "", "", "", "");
    }
}
