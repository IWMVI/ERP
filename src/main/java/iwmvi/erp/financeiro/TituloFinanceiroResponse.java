package iwmvi.erp.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TituloFinanceiroResponse(
    Long id,
    TipoTituloFinanceiro tipo,
    StatusTituloFinanceiro status,
    String pessoaNome,
    BigDecimal valor,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    int parcela,
    int totalParcelas,
    String origemTipo,
    Long origemId,
    String descricao) {
}
