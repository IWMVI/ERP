package iwmvi.erp.shared.exception;

public class DocumentoJaCadastradoException extends RuntimeException {

    public DocumentoJaCadastradoException(String documento) {
        super("O documento " + documento + " já está cadastrado.");
    }
}
