package iwmvi.erp.fornecedor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FornecedorRequest(
        @NotBlank(message="O nome é obrigatório.") String nome,
        @NotBlank(message="O CPF/CNPJ é obrigatório.") @Size(max=20) String documento,
        @Email(message="E-mail inválido.") String email,
        String telefone, String logradouro, String numero, String cidade,
        @Size(max=2, message="Informe a UF com 2 caracteres.") String estado,
        String cep) {}
