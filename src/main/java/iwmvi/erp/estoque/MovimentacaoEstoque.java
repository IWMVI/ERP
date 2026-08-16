package iwmvi.erp.estoque;

import iwmvi.erp.produto.Produto;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_estoque")
@Getter
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal quantidade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, length = 120)
    private String origem;

    @Column(nullable = false)
    private String usuario;

    protected MovimentacaoEstoque() {
    }

    public MovimentacaoEstoque(
        Produto produto,
        TipoMovimentacao tipo,
        BigDecimal quantidade,
        LocalDateTime dataHora,
        String origem,
        String usuario) {
        this.produto = produto;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
        this.origem = origem;
        this.usuario = usuario;
    }
}
