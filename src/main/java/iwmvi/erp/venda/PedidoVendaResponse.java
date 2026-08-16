package iwmvi.erp.venda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoVendaResponse(
    Long id,
    String clienteNome,
    LocalDateTime dataCriacao,
    StatusPedidoVenda status,
    BigDecimal desconto,
    BigDecimal subtotal,
    BigDecimal total,
    List<ItemPedidoVendaResponse> itens) {
}
