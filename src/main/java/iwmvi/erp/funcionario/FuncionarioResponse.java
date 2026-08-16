package iwmvi.erp.funcionario;

import java.time.LocalDate;

public record FuncionarioResponse(
    Long id,
    String nome,
    String cpf,
    String email,
    String telefone,
    String cargo,
    LocalDate dataNascimento,
    LocalDate dataAdmissao,
    String cep,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String fotoArquivo,
    boolean ativo) {
}
