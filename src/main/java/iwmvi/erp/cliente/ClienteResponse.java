package iwmvi.erp.cliente;

public record ClienteResponse(
    Long id,
    TipoPessoa tipoPessoa,
    String nome,
    String nomeFantasia,
    String documento,
    String email,
    String telefone,
    String celular,
    String cep,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String observacoes,
    boolean ativo) {
}
