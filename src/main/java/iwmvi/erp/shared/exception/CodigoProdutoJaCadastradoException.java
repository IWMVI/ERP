package iwmvi.erp.shared.exception;

public class CodigoProdutoJaCadastradoException extends RuntimeException {

    public CodigoProdutoJaCadastradoException(String codigo) {
        super("O código " + codigo + " já está cadastrado.");
    }
}
