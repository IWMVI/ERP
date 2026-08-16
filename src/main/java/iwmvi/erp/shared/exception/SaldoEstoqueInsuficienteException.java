package iwmvi.erp.shared.exception;

public class SaldoEstoqueInsuficienteException extends RuntimeException {

    public SaldoEstoqueInsuficienteException(String produto) {
        super("Saldo insuficiente para realizar a saída do produto " + produto + ".");
    }
}
