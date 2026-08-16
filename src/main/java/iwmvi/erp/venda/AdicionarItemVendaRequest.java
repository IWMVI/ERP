package iwmvi.erp.venda;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AdicionarItemVendaRequest(
        @NotNull Long produtoId,
        @NotNull @DecimalMin(value = "0.001") BigDecimal quantidade) {}
