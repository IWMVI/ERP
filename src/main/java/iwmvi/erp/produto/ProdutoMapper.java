package iwmvi.erp.produto;

import iwmvi.erp.shared.validation.DocumentoValidator;

public final class ProdutoMapper {

    private ProdutoMapper() {
    }

    public static ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
            produto.getId(),
            produto.getCodigo(),
            produto.getGtin(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getMarca(),
            produto.getCategoria(),
            produto.getSubcategoria(),
            produto.getTipo(),
            produto.getUnidadeMedida(),
            produto.getPrecoVenda(),
            produto.getCusto(),
            produto.getEstoqueMinimo(),
            produto.getEstoqueMaximo(),
            produto.getSaldoEstoque(),
            produto.getLocalizacao(),
            produto.isControlaEstoque(),
            produto.getNcm(),
            produto.getCest(),
            produto.getOrigem(),
            produto.getUnidadeTributavel(),
            produto.getFatorConversaoTributavel(),
            produto.getTipoItemSped(),
            produto.getPesoLiquido(),
            produto.getPesoBruto(),
            produto.getLargura(),
            produto.getAltura(),
            produto.getComprimento(),
            produto.getVolumes(),
            produto.getPrazoPreparacaoDias(),
            produto.getFotoArquivo(),
            produto.isAtivo(),
            produto.getVersion());
    }

    public static ProdutoRequest toRequest(Produto produto) {
        return new ProdutoRequest(
            produto.getCodigo(),
            produto.getGtin(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getMarca(),
            produto.getCategoria(),
            produto.getSubcategoria(),
            produto.getTipo(),
            produto.getUnidadeMedida(),
            produto.getPrecoVenda(),
            produto.getCusto(),
            produto.getEstoqueMinimo(),
            produto.getEstoqueMaximo(),
            produto.getLocalizacao(),
            produto.isControlaEstoque(),
            produto.getNcm() == null ? "" : produto.getNcm(),
            produto.getCest() == null ? "" : produto.getCest(),
            produto.getOrigem(),
            produto.getUnidadeTributavel(),
            produto.getFatorConversaoTributavel(),
            produto.getTipoItemSped(),
            produto.getPesoLiquido(),
            produto.getPesoBruto(),
            produto.getLargura(),
            produto.getAltura(),
            produto.getComprimento(),
            produto.getVolumes(),
            produto.getPrazoPreparacaoDias());
    }

    public static Produto toEntity(ProdutoRequest request) {
        Produto produto = new Produto();
        atualizar(produto, request);
        return produto;
    }

    public static void atualizar(Produto produto, ProdutoRequest request) {
        produto.setCodigo(request.codigo().trim());
        produto.setGtin(
            request.gtin() == null || request.gtin().isBlank()
                ? null
                : DocumentoValidator.somenteDigitos(request.gtin()));
        produto.setNome(request.nome().trim());
        produto.setDescricao(textoOpcional(request.descricao()));
        produto.setMarca(textoOpcional(request.marca()));
        produto.setCategoria(textoOpcional(request.categoria()));
        produto.setSubcategoria(textoOpcional(request.subcategoria()));
        produto.setTipo(request.tipo());
        produto.setUnidadeMedida(request.unidadeMedida());
        produto.setPrecoVenda(request.precoVenda());
        produto.setCusto(request.custo());
        produto.setEstoqueMinimo(request.estoqueMinimo());
        produto.setEstoqueMaximo(request.estoqueMaximo());
        produto.setLocalizacao(textoOpcional(request.localizacao()));
        produto.setControlaEstoque(request.controlaEstoque());
        produto.setNcm(digitosOpcionais(request.ncm()));
        produto.setCest(digitosOpcionais(request.cest()));
        produto.setOrigem(request.origem());
        produto.setUnidadeTributavel(
            request.unidadeTributavel() == null ? request.unidadeMedida() : request.unidadeTributavel());
        produto.setFatorConversaoTributavel(request.fatorConversaoTributavel());
        produto.setTipoItemSped(request.tipoItemSped());
        produto.setPesoLiquido(request.pesoLiquido());
        produto.setPesoBruto(request.pesoBruto());
        produto.setLargura(request.largura());
        produto.setAltura(request.altura());
        produto.setComprimento(request.comprimento());
        produto.setVolumes(request.volumes());
        produto.setPrazoPreparacaoDias(request.prazoPreparacaoDias());
    }

    private static String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private static String digitosOpcionais(String valor) {
        return valor == null || valor.isBlank() ? null : DocumentoValidator.somenteDigitos(valor);
    }
}
