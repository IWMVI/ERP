package iwmvi.erp.venda;

import iwmvi.erp.cliente.Cliente;
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
@Table(name = "pedidos_venda")
public class PedidoVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedidoVenda status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedidoVenda> itens = new ArrayList<>();

    protected PedidoVenda() {}

    public PedidoVenda(Cliente cliente, BigDecimal desconto) {
        this.cliente = cliente;
        this.desconto = desconto == null ? BigDecimal.ZERO : desconto;
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusPedidoVenda.RASCUNHO;
    }

    public void adicionarItem(Produto produto, BigDecimal quantidade, BigDecimal precoUnitario) {
        exigirRascunho();
        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (precoUnitario == null || precoUnitario.signum() < 0) {
            throw new IllegalArgumentException("O preço unitário não pode ser negativo.");
        }
        itens.add(new ItemPedidoVenda(this, produto, quantidade, precoUnitario));
    }

    public BigDecimal getSubtotal() {
        return itens.stream().map(ItemPedidoVenda::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotal() {
        BigDecimal total = getSubtotal().subtract(desconto);
        return total.signum() < 0 ? BigDecimal.ZERO : total;
    }

    public void concluir() {
        exigirRascunho();
        if (itens.isEmpty()) {
            throw new IllegalStateException("O pedido deve possuir ao menos um item.");
        }
        if (getTotal().signum() <= 0) {
            throw new IllegalStateException("O total da venda deve ser maior que zero.");
        }
        status = StatusPedidoVenda.CONCLUIDO;
    }

    public void cancelar() {
        exigirRascunho();
        status = StatusPedidoVenda.CANCELADO;
    }

    public void estornar() {
        if (status != StatusPedidoVenda.CONCLUIDO) {
            throw new IllegalStateException("Apenas vendas concluídas podem ser estornadas.");
        }
        status = StatusPedidoVenda.ESTORNADO;
    }

    private void exigirRascunho() {
        if (status != StatusPedidoVenda.RASCUNHO) {
            throw new IllegalStateException("O pedido não pode mais ser alterado.");
        }
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public StatusPedidoVenda getStatus() { return status; }
    public BigDecimal getDesconto() { return desconto; }
    public List<ItemPedidoVenda> getItens() { return Collections.unmodifiableList(itens); }
}
