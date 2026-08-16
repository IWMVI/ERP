package iwmvi.erp.estoque;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MovimentacaoEstoqueRequest(
    @NotNull(message = "O produto é obrigatório.") Long produtoId,
    @NotNull(message = "O tipo é obrigatório.") TipoMovimentacao tipo,
    @NotNull(message = "A quantidade é obrigatória.") @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero.") BigDecimal quantidade,
    @NotBlank(message = "A origem é obrigatória.") String origem) {
}
