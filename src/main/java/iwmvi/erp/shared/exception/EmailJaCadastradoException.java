package iwmvi.erp.shared.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("O e-mail %s já está cadastrado.".formatted(email));
    }
}
