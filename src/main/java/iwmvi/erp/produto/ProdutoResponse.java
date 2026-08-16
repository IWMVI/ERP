package iwmvi.erp.produto;

import java.math.BigDecimal;

public record ProdutoResponse(
    Long id,
    String codigo,
    String gtin,
    String nome,
    String descricao,
    String marca,
    String categoria,
    String subcategoria,
    TipoProduto tipo,
    UnidadeMedida unidadeMedida,
    BigDecimal precoVenda,
    BigDecimal custo,
    BigDecimal estoqueMinimo,
    BigDecimal estoqueMaximo,
    BigDecimal saldoEstoque,
    String localizacao,
    boolean controlaEstoque,
    String ncm,
    String cest,
    OrigemMercadoria origem,
    UnidadeMedida unidadeTributavel,
    BigDecimal fatorConversaoTributavel,
    TipoItemSped tipoItemSped,
    BigDecimal pesoLiquido,
    BigDecimal pesoBruto,
    BigDecimal largura,
    BigDecimal altura,
    BigDecimal comprimento,
    Integer volumes,
    Integer prazoPreparacaoDias,
    String fotoArquivo,
    boolean ativo,
    long version) {
}
