package iwmvi.erp.integracao;

import iwmvi.erp.shared.exception.DocumentoInvalidoException;
import iwmvi.erp.shared.validation.DocumentoValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class PessoaCadastroLookupService {

    private final BrasilApiClient brasilApiClient;

    public PessoaCadastroLookupService(BrasilApiClient brasilApiClient) {
        this.brasilApiClient = brasilApiClient;
    }

    public PessoaCadastroLookupResult consultar(String documento) {
        String normalizado = DocumentoValidator.normalizarDocumento(documento);

        if (DocumentoValidator.documentoEhCpf(normalizado)) {
            if (!DocumentoValidator.cpfValido(normalizado)) {
                throw new DocumentoInvalidoException("CPF inválido.");
            }
            return PessoaCadastroLookupResult.pessoaFisica(normalizado);
        }

        if (!DocumentoValidator.documentoEhCnpj(normalizado)
            || !DocumentoValidator.cnpjValido(normalizado)) {
            throw new DocumentoInvalidoException(
                "Informe um CPF válido ou um CNPJ válido com 14 posições.");
        }

        try {
            BrasilApiClient.CnpjDados dados = brasilApiClient.consultarCnpj(normalizado);
            if (dados == null) {
                return PessoaCadastroLookupResult.pessoaJuridicaSemConsulta(
                    normalizado,
                    "Não foi possível localizar dados públicos para este CNPJ. Preencha os dados manualmente.");
            }

            return new PessoaCadastroLookupResult(
                TipoDocumentoPessoa.CNPJ,
                normalizado,
                dados.razaoSocial(),
                dados.nomeFantasia(),
                dados.email(),
                dados.telefone(),
                dados.cep(),
                montarLogradouro(dados),
                dados.numero(),
                dados.complemento(),
                dados.bairro(),
                dados.municipio(),
                dados.uf(),
                dados.situacaoCadastral(),
                null);
        } catch (RestClientException exception) {
            return PessoaCadastroLookupResult.pessoaJuridicaSemConsulta(
                normalizado,
                "O serviço de consulta de CNPJ está indisponível. Preencha os dados manualmente.");
        }
    }

    private String montarLogradouro(BrasilApiClient.CnpjDados dados) {
        if (dados.tipoLogradouro() == null || dados.tipoLogradouro().isBlank()) {
            return dados.logradouro();
        }
        if (dados.logradouro() == null || dados.logradouro().isBlank()) {
            return dados.tipoLogradouro();
        }
        return dados.tipoLogradouro() + " " + dados.logradouro();
    }

    public enum TipoDocumentoPessoa {
        CPF,
        CNPJ
    }

    public record PessoaCadastroLookupResult(
        TipoDocumentoPessoa tipoDocumento,
        String documento,
        String nome,
        String nomeFantasia,
        String email,
        String telefone,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String situacaoCadastral,
        String aviso) {

        public static PessoaCadastroLookupResult pessoaFisica(String documento) {
            return new PessoaCadastroLookupResult(
                TipoDocumentoPessoa.CPF,
                documento,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                null,
                null);
        }

        public static PessoaCadastroLookupResult pessoaJuridicaSemConsulta(
            String documento, String aviso) {
            return new PessoaCadastroLookupResult(
                TipoDocumentoPessoa.CNPJ,
                documento,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                null,
                aviso);
        }

        public boolean pessoaFisica() {
            return tipoDocumento == TipoDocumentoPessoa.CPF;
        }

        public boolean pessoaJuridica() {
            return tipoDocumento == TipoDocumentoPessoa.CNPJ;
        }
    }
}
