package iwmvi.erp.produto;

public enum TipoItemSped {
    MERCADORIA_REVENDA("00 - Mercadoria para revenda"),
    MATERIA_PRIMA("01 - Matéria-prima"),
    EMBALAGEM("02 - Embalagem"),
    PRODUTO_PROCESSO("03 - Produto em processo"),
    PRODUTO_ACABADO("04 - Produto acabado"),
    SUBPRODUTO("05 - Subproduto"),
    PRODUTO_INTERMEDIARIO("06 - Produto intermediário"),
    MATERIAL_USO_CONSUMO("07 - Material de uso e consumo"),
    ATIVO_IMOBILIZADO("08 - Ativo imobilizado"),
    SERVICO("09 - Serviço"),
    OUTROS_INSUMOS("10 - Outros insumos"),
    OUTRAS("99 - Outras");

    private final String descricao;

    TipoItemSped(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
