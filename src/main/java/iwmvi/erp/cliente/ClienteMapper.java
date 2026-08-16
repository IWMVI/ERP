package iwmvi.erp.cliente;

import iwmvi.erp.shared.validation.DocumentoValidator;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
            cliente.getId(),
            cliente.getTipoPessoa(),
            cliente.getNome(),
            cliente.getNomeFantasia(),
            cliente.getDocumento(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getCelular(),
            cliente.getCep(),
            cliente.getLogradouro(),
            cliente.getNumero(),
            cliente.getComplemento(),
            cliente.getBairro(),
            cliente.getCidade(),
            cliente.getEstado(),
            cliente.getObservacoes(),
            cliente.isAtivo());
    }

    public static ClienteRequest toRequest(Cliente cliente) {
        return new ClienteRequest(
            cliente.getTipoPessoa(),
            cliente.getNome(),
            cliente.getNomeFantasia(),
            cliente.getDocumento(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getCelular(),
            cliente.getCep(),
            cliente.getLogradouro(),
            cliente.getNumero(),
            cliente.getComplemento(),
            cliente.getBairro(),
            cliente.getCidade(),
            cliente.getEstado(),
            cliente.getObservacoes());
    }

    public static Cliente toEntity(ClienteRequest request) {
        Cliente cliente = new Cliente();
        atualizar(cliente, request);
        return cliente;
    }

    public static void atualizar(Cliente cliente, ClienteRequest request) {
        String documentoNormalizado = DocumentoValidator.normalizarDocumento(request.documento());
        cliente.setTipoPessoa(
            DocumentoValidator.documentoEhCpf(documentoNormalizado)
                ? TipoPessoa.FISICA
                : TipoPessoa.JURIDICA);
        cliente.setNome(request.nome().trim());
        cliente.setNomeFantasia(
            cliente.getTipoPessoa() == TipoPessoa.JURIDICA ? request.nomeFantasia() : null);
        cliente.setDocumento(documentoNormalizado);
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        cliente.setCelular(request.celular());
        cliente.setCep(DocumentoValidator.somenteDigitos(request.cep()));
        cliente.setLogradouro(request.logradouro());
        cliente.setNumero(request.numero());
        cliente.setComplemento(request.complemento());
        cliente.setBairro(request.bairro());
        cliente.setCidade(request.cidade());
        cliente.setEstado(request.estado());
        cliente.setObservacoes(request.observacoes());
    }
}
