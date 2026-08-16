package iwmvi.erp.produto;

public enum TipoProduto {
    PRODUTO("Produto simples"),
    SERVICO("Serviço"),
    KIT("Kit / composição"),
    MATERIA_PRIMA("Matéria-prima"),
    COM_VARIACOES("Produto com variações");

    private final String descricao;

    TipoProduto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
