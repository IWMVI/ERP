package iwmvi.erp.venda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import iwmvi.erp.cliente.Cliente;
import iwmvi.erp.produto.Produto;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PedidoVendaTest {

    @Test
    void deveCalcularTotalEConcluirPedido() {
        Cliente cliente = mock(Cliente.class);
        Produto produto = mock(Produto.class);
        PedidoVenda pedido = new PedidoVenda(cliente, new BigDecimal("10.00"));

        pedido.adicionarItem(produto, new BigDecimal("2.000"), new BigDecimal("50.00"));
        pedido.concluir();

        assertEquals(new BigDecimal("100.00000"), pedido.getSubtotal());
        assertEquals(new BigDecimal("90.00000"), pedido.getTotal());
        assertEquals(StatusPedidoVenda.CONCLUIDO, pedido.getStatus());
        assertThrows(
                IllegalStateException.class,
                () -> pedido.adicionarItem(produto, BigDecimal.ONE, BigDecimal.TEN));
    }

    @Test
    void naoDeveConcluirPedidoSemItens() {
        PedidoVenda pedido = new PedidoVenda(mock(Cliente.class), BigDecimal.ZERO);

        assertThrows(IllegalStateException.class, pedido::concluir);
    }
}
