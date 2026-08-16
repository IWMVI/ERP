package iwmvi.erp.financeiro;

import iwmvi.erp.cliente.Cliente;
import iwmvi.erp.fornecedor.Fornecedor;
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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "titulos_financeiros")
public class TituloFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoTituloFinanceiro tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusTituloFinanceiro status = StatusTituloFinanceiro.ABERTO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(nullable = false)
    private int parcela;

    @Column(name = "total_parcelas", nullable = false)
    private int totalParcelas;

    @Column(name = "origem_tipo", nullable = false, length = 30)
    private String origemTipo;

    @Column(name = "origem_id", nullable = false)
    private Long origemId;

    @Column(nullable = false, length = 200)
    private String descricao;

    protected TituloFinanceiro() {}

    TituloFinanceiro(
            TipoTituloFinanceiro tipo,
            Cliente cliente,
            Fornecedor fornecedor,
            BigDecimal valor,
            LocalDate dataVencimento,
            int parcela,
            int totalParcelas,
            String origemTipo,
            Long origemId,
            String descricao) {
        this.tipo = tipo;
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.parcela = parcela;
        this.totalParcelas = totalParcelas;
        this.origemTipo = origemTipo;
        this.origemId = origemId;
        this.descricao = descricao;
    }

    public void pagar(LocalDate dataPagamento) {
        if (status != StatusTituloFinanceiro.ABERTO) {
            throw new IllegalStateException("Apenas títulos em aberto podem ser pagos.");
        }
        this.status = StatusTituloFinanceiro.PAGO;
        this.dataPagamento = dataPagamento;
    }

    public boolean isVencido(LocalDate hoje) {
        return status == StatusTituloFinanceiro.ABERTO && dataVencimento.isBefore(hoje);
    }

    public Long getId() { return id; }
    public TipoTituloFinanceiro getTipo() { return tipo; }
    public StatusTituloFinanceiro getStatus() { return status; }
    public Cliente getCliente() { return cliente; }
    public Fornecedor getFornecedor() { return fornecedor; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public int getParcela() { return parcela; }
    public int getTotalParcelas() { return totalParcelas; }
    public String getOrigemTipo() { return origemTipo; }
    public Long getOrigemId() { return origemId; }
    public String getDescricao() { return descricao; }
}
