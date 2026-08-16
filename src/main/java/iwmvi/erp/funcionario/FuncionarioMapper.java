package iwmvi.erp.funcionario;

import iwmvi.erp.shared.validation.DocumentoValidator;

public final class FuncionarioMapper {

    private FuncionarioMapper() {
    }

    public static FuncionarioResponse toResponse(Funcionario funcionario) {
        return new FuncionarioResponse(
            funcionario.getId(),
            funcionario.getNome(),
            funcionario.getCpf(),
            funcionario.getEmail(),
            funcionario.getTelefone(),
            funcionario.getCargo(),
            funcionario.getDataNascimento(),
            funcionario.getDataAdmissao(),
            funcionario.getCep(),
            funcionario.getLogradouro(),
            funcionario.getNumero(),
            funcionario.getComplemento(),
            funcionario.getBairro(),
            funcionario.getCidade(),
            funcionario.getEstado(),
            funcionario.getFotoArquivo(),
            funcionario.isAtivo());
    }

    public static FuncionarioRequest toRequest(Funcionario funcionario) {
        return new FuncionarioRequest(
            funcionario.getNome(),
            funcionario.getCpf(),
            funcionario.getEmail(),
            funcionario.getTelefone(),
            funcionario.getCargo(),
            funcionario.getDataNascimento(),
            funcionario.getDataAdmissao(),
            funcionario.getCep(),
            funcionario.getLogradouro(),
            funcionario.getNumero(),
            funcionario.getComplemento(),
            funcionario.getBairro(),
            funcionario.getCidade(),
            funcionario.getEstado());
    }

    public static Funcionario toEntity(FuncionarioRequest request) {
        Funcionario funcionario = new Funcionario();
        atualizar(funcionario, request);
        return funcionario;
    }

    public static void atualizar(Funcionario funcionario, FuncionarioRequest request) {
        funcionario.setNome(request.nome().trim());
        funcionario.setCpf(DocumentoValidator.somenteDigitos(request.cpf()));
        funcionario.setEmail(request.email());
        funcionario.setTelefone(request.telefone());
        funcionario.setCargo(request.cargo().trim());
        funcionario.setDataNascimento(request.dataNascimento());
        funcionario.setDataAdmissao(request.dataAdmissao());
        funcionario.setCep(DocumentoValidator.somenteDigitos(request.cep()));
        funcionario.setLogradouro(request.logradouro());
        funcionario.setNumero(request.numero());
        funcionario.setComplemento(request.complemento());
        funcionario.setBairro(request.bairro());
        funcionario.setCidade(request.cidade());
        funcionario.setEstado(request.estado());
    }
}
