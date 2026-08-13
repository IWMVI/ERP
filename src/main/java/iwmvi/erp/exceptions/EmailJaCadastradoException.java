package iwmvi.erp.exceptions;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailJaCadastradoException extends Exception {
    public EmailJaCadastradoException(
            @NotBlank(message = "O e-mail é obrigatório.") @Email(message = "O e-mail deve ser válido.") String email) {
        super(String.format("O e-mail %s já está cadastrado.", email));
    }
}
