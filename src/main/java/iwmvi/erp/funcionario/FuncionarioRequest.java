package iwmvi.erp.funcionario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record FuncionarioRequest(
        @NotBlank(message = "O nome é obrigatório.") String nome,
        @NotBlank(message = "O CPF é obrigatório.") String cpf,
        @Email(message = "E-mail inválido.") String email,
        String telefone,
        @NotBlank(message = "O cargo é obrigatório.") String cargo,
        LocalDate dataNascimento,
        @NotNull(message = "A data de admissão é obrigatória.") LocalDate dataAdmissao,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        @Size(max = 2, message = "Informe a UF com 2 caracteres.") String estado) {}
