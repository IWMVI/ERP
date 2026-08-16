package iwmvi.erp.fornecedor;

public record FornecedorResponse(
    Long id,
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
