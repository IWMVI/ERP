package iwmvi.erp.venda;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CriarPedidoVendaRequest(
        @NotNull Long clienteId,
        @DecimalMin(value = "0.00") BigDecimal desconto) {}
