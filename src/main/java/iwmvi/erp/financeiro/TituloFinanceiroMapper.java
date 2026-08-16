package iwmvi.erp.financeiro;

public final class TituloFinanceiroMapper {

    private TituloFinanceiroMapper() {
    }

    public static TituloFinanceiroResponse toResponse(TituloFinanceiro titulo) {
        return new TituloFinanceiroResponse(
            titulo.getId(),
            titulo.getTipo(),
            titulo.getStatus(),
            pessoaNome(titulo),
            titulo.getValor(),
            titulo.getDataVencimento(),
            titulo.getDataPagamento(),
            titulo.getParcela(),
            titulo.getTotalParcelas(),
            titulo.getOrigemTipo(),
            titulo.getOrigemId(),
            titulo.getDescricao());
    }

    private static String pessoaNome(TituloFinanceiro titulo) {
        if (titulo.getCliente() != null) {
            return titulo.getCliente().getNome();
        }
        return titulo.getFornecedor() == null ? null : titulo.getFornecedor().getNome();
    }
}
