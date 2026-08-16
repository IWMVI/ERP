package iwmvi.erp.compra;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AdicionarItemCompraRequest(
    @NotNull Long produtoId,
    @NotNull @DecimalMin(value = "0.001") BigDecimal quantidade,
    @NotNull @DecimalMin(value = "0.00") BigDecimal custoUnitario) {
}
