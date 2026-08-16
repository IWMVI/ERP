package iwmvi.erp.compra;

import iwmvi.erp.produto.Produto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido_compra")
public class ItemPedidoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoCompra pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantidade;

    @Column(name = "custo_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal custoUnitario;

    protected ItemPedidoCompra() {}

    ItemPedidoCompra(PedidoCompra pedido, Produto produto, BigDecimal quantidade, BigDecimal custoUnitario) {
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.custoUnitario = custoUnitario;
    }

    public BigDecimal subtotal() { return custoUnitario.multiply(quantidade); }
    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public BigDecimal getQuantidade() { return quantidade; }
    public BigDecimal getCustoUnitario() { return custoUnitario; }
}
