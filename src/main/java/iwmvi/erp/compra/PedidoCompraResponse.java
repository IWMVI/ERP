package iwmvi.erp.compra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoCompraResponse(
    Long id,
    String fornecedorNome,
    LocalDateTime dataCriacao,
    StatusPedidoCompra status,
    BigDecimal total,
    List<ItemPedidoCompraResponse> itens) {
}
