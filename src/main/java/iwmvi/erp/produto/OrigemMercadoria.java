package iwmvi.erp.produto;

public enum OrigemMercadoria {
    NACIONAL("0 - Nacional"),
    ESTRANGEIRA_IMPORTACAO_DIRETA("1 - Estrangeira, importação direta"),
    ESTRANGEIRA_MERCADO_INTERNO("2 - Estrangeira, adquirida no mercado interno"),
    NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_40("3 - Nacional, conteúdo de importação superior a 40%"),
    NACIONAL_PROCESSOS_BASICOS("4 - Nacional, conforme processos produtivos básicos"),
    NACIONAL_CONTEUDO_IMPORTACAO_INFERIOR_40("5 - Nacional, conteúdo de importação até 40%"),
    ESTRANGEIRA_SEM_SIMILAR_IMPORTACAO_DIRETA("6 - Estrangeira sem similar, importação direta"),
    ESTRANGEIRA_SEM_SIMILAR_MERCADO_INTERNO("7 - Estrangeira sem similar, mercado interno"),
    NACIONAL_CONTEUDO_IMPORTACAO_SUPERIOR_70("8 - Nacional, conteúdo de importação superior a 70%");

    private final String descricao;

    OrigemMercadoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
