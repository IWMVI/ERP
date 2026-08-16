package iwmvi.erp.produto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank(message="O código é obrigatório.") String codigo,
        @NotBlank(message="O nome é obrigatório.") String nome,
        String categoria,
        @NotNull(message="A unidade é obrigatória.") UnidadeMedida unidadeMedida,
        @NotNull @DecimalMin(value="0.00") BigDecimal precoVenda,
        @NotNull @DecimalMin(value="0.00") BigDecimal custo,
        @NotNull @DecimalMin(value="0.000") BigDecimal estoqueMinimo) {}
