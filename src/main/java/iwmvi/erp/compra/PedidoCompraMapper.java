package iwmvi.erp.compra;

import java.util.List;

public final class PedidoCompraMapper {

    private PedidoCompraMapper() {
    }

    public static PedidoCompraResponse toResponse(PedidoCompra pedido) {
        List<ItemPedidoCompraResponse> itens =
            pedido.getItens().stream().map(PedidoCompraMapper::itemToResponse).toList();
        return toResponse(pedido, itens);
    }

    public static PedidoCompraResponse toResumo(PedidoCompra pedido) {
        return toResponse(pedido, List.of());
    }

    private static PedidoCompraResponse toResponse(
        PedidoCompra pedido, List<ItemPedidoCompraResponse> itens) {
        return new PedidoCompraResponse(
            pedido.getId(),
            pedido.getFornecedor().getNome(),
            pedido.getDataCriacao(),
            pedido.getStatus(),
            pedido.getTotal(),
            itens);
    }

    private static ItemPedidoCompraResponse itemToResponse(ItemPedidoCompra item) {
        return new ItemPedidoCompraResponse(
            item.getId(),
            item.getProduto().getId(),
            item.getProduto().getCodigo(),
            item.getProduto().getNome(),
            item.getQuantidade(),
            item.getCustoUnitario(),
            item.subtotal());
    }
}
