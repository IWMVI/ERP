package iwmvi.erp.compra;

import iwmvi.erp.fornecedor.Fornecedor;
import iwmvi.erp.produto.Produto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class PedidoCompraTest {

    @Test
    void deveCalcularTotalEReceberPedido() {
        PedidoCompra pedido = new PedidoCompra(mock(Fornecedor.class));
        Produto produto = mock(Produto.class);

        pedido.adicionarItem(produto, new BigDecimal("3.000"), new BigDecimal("20.00"));
        pedido.receber();

        assertEquals(new BigDecimal("60.00000"), pedido.getTotal());
        assertEquals(StatusPedidoCompra.RECEBIDO, pedido.getStatus());
        assertThrows(
            IllegalStateException.class,
            () -> pedido.adicionarItem(produto, BigDecimal.ONE, BigDecimal.TEN));
    }

    @Test
    void naoDeveReceberPedidoSemItens() {
        PedidoCompra pedido = new PedidoCompra(mock(Fornecedor.class));

        assertThrows(IllegalStateException.class, pedido::receber);
    }
}
