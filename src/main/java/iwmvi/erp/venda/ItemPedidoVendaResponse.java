package iwmvi.erp.venda;

import java.math.BigDecimal;

public record ItemPedidoVendaResponse(
    Long id,
    Long produtoId,
    String produtoCodigo,
    String produtoNome,
    BigDecimal quantidade,
    BigDecimal precoUnitario,
    BigDecimal subtotal) {
}
