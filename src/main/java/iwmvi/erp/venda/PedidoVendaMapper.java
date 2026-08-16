package iwmvi.erp.venda;

import java.util.List;

public final class PedidoVendaMapper {

    private PedidoVendaMapper() {
    }

    public static PedidoVendaResponse toResponse(PedidoVenda pedido) {
        List<ItemPedidoVendaResponse> itens =
            pedido.getItens().stream().map(PedidoVendaMapper::itemToResponse).toList();
        return toResponse(pedido, itens);
    }

    public static PedidoVendaResponse toResumo(PedidoVenda pedido) {
        return toResponse(pedido, List.of());
    }

    private static PedidoVendaResponse toResponse(
        PedidoVenda pedido, List<ItemPedidoVendaResponse> itens) {
        return new PedidoVendaResponse(
            pedido.getId(),
            pedido.getCliente().getNome(),
            pedido.getDataCriacao(),
            pedido.getStatus(),
            pedido.getDesconto(),
            pedido.getSubtotal(),
            pedido.getTotal(),
            itens);
    }

    private static ItemPedidoVendaResponse itemToResponse(ItemPedidoVenda item) {
        return new ItemPedidoVendaResponse(
            item.getId(),
            item.getProduto().getId(),
            item.getProduto().getCodigo(),
            item.getProduto().getNome(),
            item.getQuantidade(),
            item.getPrecoUnitario(),
            item.subtotal());
    }
}
