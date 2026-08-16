package iwmvi.erp.financeiro;

import iwmvi.erp.cliente.Cliente;
import iwmvi.erp.fornecedor.Fornecedor;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "titulos_financeiros")
@Getter
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

    protected TituloFinanceiro() {
    }

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

    public void cancelar() {
        if (status == StatusTituloFinanceiro.PAGO) {
            throw new IllegalStateException("Não é possível cancelar um título já pago.");
        }
        this.status = StatusTituloFinanceiro.CANCELADO;
    }

    public boolean isVencido(LocalDate hoje) {
        return status == StatusTituloFinanceiro.ABERTO && dataVencimento.isBefore(hoje);
    }
}
