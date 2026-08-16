package iwmvi.erp.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotNull(message = "O tipo de pessoa é obrigatório.") TipoPessoa tipoPessoa,
        @NotBlank(message = "O nome é obrigatório.") String nome,
        String nomeFantasia,
        @NotBlank(message = "O CPF/CNPJ é obrigatório.") @Size(max = 20) String documento,
        @Email(message = "E-mail inválido.") String email,
        String telefone,
        String celular,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        @Size(max = 2, message = "Informe a UF com 2 caracteres.") String estado,
        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres.") String observacoes) {

    public ClienteRequest(
            TipoPessoa tipoPessoa,
            String nome,
            String documento,
            String email,
            String telefone,
            String logradouro,
            String numero,
            String cidade,
            String estado,
            String cep) {
        this(
                tipoPessoa,
                nome,
                "",
                documento,
                email,
                telefone,
                "",
                cep,
                logradouro,
                numero,
                "",
                "",
                cidade,
                estado,
                "");
    }
}
