package iwmvi.erp.fornecedor;

import iwmvi.erp.shared.validation.DocumentoValidator;

public final class FornecedorMapper {

    private FornecedorMapper() {
    }

    public static FornecedorResponse toResponse(Fornecedor fornecedor) {
        return new FornecedorResponse(
            fornecedor.getId(),
            fornecedor.getNome(),
            fornecedor.getNomeFantasia(),
            fornecedor.getDocumento(),
            fornecedor.getEmail(),
            fornecedor.getTelefone(),
            fornecedor.getCelular(),
            fornecedor.getCep(),
            fornecedor.getLogradouro(),
            fornecedor.getNumero(),
            fornecedor.getComplemento(),
            fornecedor.getBairro(),
            fornecedor.getCidade(),
            fornecedor.getEstado(),
            fornecedor.getObservacoes(),
            fornecedor.isAtivo());
    }

    public static FornecedorRequest toRequest(Fornecedor fornecedor) {
        return new FornecedorRequest(
            fornecedor.getNome(),
            fornecedor.getNomeFantasia(),
            fornecedor.getDocumento(),
            fornecedor.getEmail(),
            fornecedor.getTelefone(),
            fornecedor.getCelular(),
            fornecedor.getCep(),
            fornecedor.getLogradouro(),
            fornecedor.getNumero(),
            fornecedor.getComplemento(),
            fornecedor.getBairro(),
            fornecedor.getCidade(),
            fornecedor.getEstado(),
            fornecedor.getObservacoes());
    }

    public static Fornecedor toEntity(FornecedorRequest request) {
        Fornecedor fornecedor = new Fornecedor();
        atualizar(fornecedor, request);
        return fornecedor;
    }

    public static void atualizar(Fornecedor fornecedor, FornecedorRequest request) {
        fornecedor.setNome(request.nome().trim());
        fornecedor.setNomeFantasia(request.nomeFantasia());
        fornecedor.setDocumento(DocumentoValidator.normalizarDocumento(request.documento()));
        fornecedor.setEmail(request.email());
        fornecedor.setTelefone(request.telefone());
        fornecedor.setCelular(request.celular());
        fornecedor.setCep(DocumentoValidator.somenteDigitos(request.cep()));
        fornecedor.setLogradouro(request.logradouro());
        fornecedor.setNumero(request.numero());
        fornecedor.setComplemento(request.complemento());
        fornecedor.setBairro(request.bairro());
        fornecedor.setCidade(request.cidade());
        fornecedor.setEstado(request.estado());
        fornecedor.setObservacoes(request.observacoes());
    }
}
