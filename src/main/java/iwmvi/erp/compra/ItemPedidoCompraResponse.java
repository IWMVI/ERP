package iwmvi.erp.compra;

import java.math.BigDecimal;

public record ItemPedidoCompraResponse(
    Long id,
    Long produtoId,
    String produtoCodigo,
    String produtoNome,
    BigDecimal quantidade,
    BigDecimal custoUnitario,
    BigDecimal subtotal) {
}
