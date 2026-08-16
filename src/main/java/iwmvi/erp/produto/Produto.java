package iwmvi.erp.produto;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "produtos",
    uniqueConstraints = @UniqueConstraint(name = "uk_produto_codigo", columnNames = "codigo"))
@Getter
@Setter(AccessLevel.PACKAGE)
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String codigo;

    @Column(length = 14)
    private String gtin;

    @Column(nullable = false)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    private String marca;
    private String categoria;
    private String subcategoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoProduto tipo = TipoProduto.PRODUTO;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false)
    private UnidadeMedida unidadeMedida;

    @Column(name = "preco_venda", nullable = false, precision = 19, scale = 2)
    private BigDecimal precoVenda;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal custo;

    @Column(name = "estoque_minimo", nullable = false, precision = 19, scale = 3)
    private BigDecimal estoqueMinimo = BigDecimal.ZERO;

    @Column(name = "estoque_maximo", precision = 19, scale = 3)
    private BigDecimal estoqueMaximo = BigDecimal.ZERO;

    @Column(name = "saldo_estoque", nullable = false, precision = 19, scale = 3)
    @Setter(AccessLevel.NONE)
    private BigDecimal saldoEstoque = BigDecimal.ZERO;

    private String localizacao;

    @Column(name = "controla_estoque", nullable = false)
    private boolean controlaEstoque = true;

    @Column(length = 8)
    private String ncm;

    @Column(length = 7)
    private String cest;

    @Enumerated(EnumType.STRING)
    @Column(length = 60)
    private OrigemMercadoria origem;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_tributavel", length = 30)
    private UnidadeMedida unidadeTributavel;

    @Column(name = "fator_conversao_tributavel", nullable = false, precision = 19, scale = 6)
    private BigDecimal fatorConversaoTributavel = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item_sped", length = 40)
    private TipoItemSped tipoItemSped;

    @Column(name = "peso_liquido", precision = 12, scale = 3)
    private BigDecimal pesoLiquido;

    @Column(name = "peso_bruto", precision = 12, scale = 3)
    private BigDecimal pesoBruto;

    @Column(precision = 12, scale = 2)
    private BigDecimal largura;

    @Column(precision = 12, scale = 2)
    private BigDecimal altura;

    @Column(precision = 12, scale = 2)
    private BigDecimal comprimento;

    private Integer volumes;

    @Column(name = "prazo_preparacao_dias")
    private Integer prazoPreparacaoDias;

    @Column(name = "foto_arquivo", length = 255)
    private String fotoArquivo;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private boolean ativo = true;

    @Version
    @Setter(AccessLevel.NONE)
    private long version;

    protected Produto() {
    }

    public void definirFotoArquivo(String fotoArquivo) {
        this.fotoArquivo = fotoArquivo;
    }

    public void alternarAtivo() {
        this.ativo = !this.ativo;
    }

    public void definirSaldo(BigDecimal saldo) {
        this.saldoEstoque = saldo;
    }
}
