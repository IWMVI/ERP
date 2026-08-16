package iwmvi.erp.produto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank(message = "O código é obrigatório.") String codigo,
        String gtin,
        @NotBlank(message = "O nome é obrigatório.") String nome,
        String descricao,
        String marca,
        String categoria,
        String subcategoria,
        @NotNull(message = "A unidade é obrigatória.") UnidadeMedida unidadeMedida,
        @NotNull @DecimalMin(value = "0.00") BigDecimal precoVenda,
        @NotNull @DecimalMin(value = "0.00") BigDecimal custo,
        @NotNull @DecimalMin(value = "0.000") BigDecimal estoqueMinimo,
        @DecimalMin(value = "0.000") BigDecimal estoqueMaximo,
        String localizacao) {

    public ProdutoRequest(
            String codigo,
            String nome,
            String categoria,
            UnidadeMedida unidadeMedida,
            BigDecimal precoVenda,
            BigDecimal custo,
            BigDecimal estoqueMinimo) {
        this(
                codigo,
                "",
                nome,
                "",
                "",
                categoria,
                "",
                unidadeMedida,
                precoVenda,
                custo,
                estoqueMinimo,
                BigDecimal.ZERO,
                "");
    }
}
