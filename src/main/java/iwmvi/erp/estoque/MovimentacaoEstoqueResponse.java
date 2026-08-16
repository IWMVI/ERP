package iwmvi.erp.estoque;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponse(
    Long id,
    Long produtoId,
    String produtoCodigo,
    String produtoNome,
    TipoMovimentacao tipo,
    BigDecimal quantidade,
    LocalDateTime dataHora,
    String origem,
    String usuario) {
}
