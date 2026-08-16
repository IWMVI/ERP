package iwmvi.erp.compra;

import iwmvi.erp.produto.Produto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido_compra")
@Getter
public class ItemPedidoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    @Getter(AccessLevel.NONE)
    private PedidoCompra pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantidade;

    @Column(name = "custo_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoUnitario;

    protected ItemPedidoCompra() {
    }

    ItemPedidoCompra(
        PedidoCompra pedido, Produto produto, BigDecimal quantidade, BigDecimal custoUnitario) {
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.custoUnitario = custoUnitario;
    }

    public BigDecimal subtotal() {
        return custoUnitario.multiply(quantidade);
    }
}
