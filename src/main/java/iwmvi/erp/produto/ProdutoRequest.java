package iwmvi.erp.produto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoRequest(
    @NotBlank(message = "O código é obrigatório.") @Size(max = 80) String codigo,
    String gtin,
    @NotBlank(message = "O nome é obrigatório.") String nome,
    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.") String descricao,
    String marca,
    String categoria,
    String subcategoria,
    @NotNull(message = "O tipo é obrigatório.") TipoProduto tipo,
    @NotNull(message = "A unidade é obrigatória.") UnidadeMedida unidadeMedida,
    @NotNull(message = "O preço de venda é obrigatório.") @DecimalMin(value = "0.00", message = "O preço não pode ser negativo.") BigDecimal precoVenda,
    @NotNull(message = "O custo é obrigatório.") @DecimalMin(value = "0.00", message = "O custo não pode ser negativo.") BigDecimal custo,
    @NotNull @DecimalMin(value = "0.000", message = "O estoque mínimo não pode ser negativo.") BigDecimal estoqueMinimo,
    @DecimalMin(value = "0.000", message = "O estoque máximo não pode ser negativo.") BigDecimal estoqueMaximo,
    String localizacao,
    boolean controlaEstoque,
    @Pattern(regexp = "^$|\\d{8}$", message = "O NCM deve conter 8 dígitos.") String ncm,
    @Pattern(regexp = "^$|\\d{7}$", message = "O CEST deve conter 7 dígitos.") String cest,
    OrigemMercadoria origem,
    UnidadeMedida unidadeTributavel,
    @NotNull(message = "O fator de conversão é obrigatório.") @DecimalMin(value = "0.000001", message = "O fator de conversão deve ser maior que zero.") BigDecimal fatorConversaoTributavel,
    TipoItemSped tipoItemSped,
    @DecimalMin(value = "0.000", message = "O peso líquido não pode ser negativo.") BigDecimal pesoLiquido,
    @DecimalMin(value = "0.000", message = "O peso bruto não pode ser negativo.") BigDecimal pesoBruto,
    @DecimalMin(value = "0.00", message = "A largura não pode ser negativa.") BigDecimal largura,
    @DecimalMin(value = "0.00", message = "A altura não pode ser negativa.") BigDecimal altura,
    @DecimalMin(value = "0.00", message = "O comprimento não pode ser negativo.") BigDecimal comprimento,
    @Min(value = 1, message = "A quantidade de volumes deve ser maior que zero.") Integer volumes,
    @Min(value = 0, message = "O prazo de preparação não pode ser negativo.") Integer prazoPreparacaoDias) {

    public ProdutoRequest(
        String codigo,
        String gtin,
        String nome,
        String descricao,
        String marca,
        String categoria,
        String subcategoria,
        UnidadeMedida unidadeMedida,
        BigDecimal precoVenda,
        BigDecimal custo,
        BigDecimal estoqueMinimo,
        BigDecimal estoqueMaximo,
        String localizacao) {
        this(
            codigo,
            gtin,
            nome,
            descricao,
            marca,
            categoria,
            subcategoria,
            TipoProduto.PRODUTO,
            unidadeMedida,
            precoVenda,
            custo,
            estoqueMinimo,
            estoqueMaximo,
            localizacao,
            true,
            "",
            "",
            null,
            unidadeMedida,
            BigDecimal.ONE,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    }

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
