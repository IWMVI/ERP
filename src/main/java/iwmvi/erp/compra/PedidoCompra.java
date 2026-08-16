package iwmvi.erp.compra;

import iwmvi.erp.fornecedor.Fornecedor;
import iwmvi.erp.produto.Produto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "pedidos_compra")
public class PedidoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedidoCompra status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedidoCompra> itens = new ArrayList<>();

    protected PedidoCompra() {}

    public PedidoCompra(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusPedidoCompra.RASCUNHO;
    }

    public void adicionarItem(Produto produto, BigDecimal quantidade, BigDecimal custoUnitario) {
        exigirRascunho();
        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (custoUnitario == null || custoUnitario.signum() < 0) {
            throw new IllegalArgumentException("O custo unitário não pode ser negativo.");
        }
        itens.add(new ItemPedidoCompra(this, produto, quantidade, custoUnitario));
    }

    public BigDecimal getTotal() {
        return itens.stream().map(ItemPedidoCompra::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void receber() {
        exigirRascunho();
        if (itens.isEmpty()) {
            throw new IllegalStateException("O pedido deve possuir ao menos um item.");
        }
        if (getTotal().signum() <= 0) {
            throw new IllegalStateException("O total da compra deve ser maior que zero.");
        }
        status = StatusPedidoCompra.RECEBIDO;
    }

    public void cancelar() {
        exigirRascunho();
        status = StatusPedidoCompra.CANCELADO;
    }

    public void estornar() {
        if (status != StatusPedidoCompra.RECEBIDO) {
            throw new IllegalStateException("Apenas compras recebidas podem ser estornadas.");
        }
        status = StatusPedidoCompra.ESTORNADO;
    }

    private void exigirRascunho() {
        if (status != StatusPedidoCompra.RASCUNHO) {
            throw new IllegalStateException("O pedido não pode mais ser alterado.");
        }
    }

    public Long getId() { return id; }
    public Fornecedor getFornecedor() { return fornecedor; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public StatusPedidoCompra getStatus() { return status; }
    public List<ItemPedidoCompra> getItens() { return Collections.unmodifiableList(itens); }
}
