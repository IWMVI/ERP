package iwmvi.erp.estoque;

public final class MovimentacaoEstoqueMapper {

    private MovimentacaoEstoqueMapper() {
    }

    public static MovimentacaoEstoqueResponse toResponse(MovimentacaoEstoque movimentacao) {
        return new MovimentacaoEstoqueResponse(
            movimentacao.getId(),
            movimentacao.getProduto().getId(),
            movimentacao.getProduto().getCodigo(),
            movimentacao.getProduto().getNome(),
            movimentacao.getTipo(),
            movimentacao.getQuantidade(),
            movimentacao.getDataHora(),
            movimentacao.getOrigem(),
            movimentacao.getUsuario());
    }
}
