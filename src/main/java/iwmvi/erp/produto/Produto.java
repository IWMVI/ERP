package iwmvi.erp.produto;

import iwmvi.erp.shared.validation.DocumentoValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigDecimal;

@Entity
@Table(
        name = "produtos",
        uniqueConstraints = @UniqueConstraint(name = "uk_produto_codigo", columnNames = "codigo"))
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private BigDecimal saldoEstoque = BigDecimal.ZERO;

    private String localizacao;

    @Column(nullable = false)
    private boolean ativo = true;

    @Version private long version;

    protected Produto() {}

    public Produto(ProdutoRequest request) {
        atualizar(request);
    }

    public void atualizar(ProdutoRequest request) {
        this.codigo = request.codigo().trim();
        this.gtin = request.gtin() == null || request.gtin().isBlank()
                ? null
                : DocumentoValidator.somenteDigitos(request.gtin());
        this.nome = request.nome().trim();
        this.descricao = request.descricao();
        this.marca = request.marca();
        this.categoria = request.categoria();
        this.subcategoria = request.subcategoria();
        this.unidadeMedida = request.unidadeMedida();
        this.precoVenda = request.precoVenda();
        this.custo = request.custo();
        this.estoqueMinimo = request.estoqueMinimo();
        this.estoqueMaximo = request.estoqueMaximo();
        this.localizacao = request.localizacao();
    }

    public void alternarAtivo() { this.ativo = !this.ativo; }
    public void definirSaldo(BigDecimal saldo) { this.saldoEstoque = saldo; }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getGtin() { return gtin; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getMarca() { return marca; }
    public String getCategoria() { return categoria; }
    public String getSubcategoria() { return subcategoria; }
    public UnidadeMedida getUnidadeMedida() { return unidadeMedida; }
    public BigDecimal getPrecoVenda() { return precoVenda; }
    public BigDecimal getCusto() { return custo; }
    public BigDecimal getEstoqueMinimo() { return estoqueMinimo; }
    public BigDecimal getEstoqueMaximo() { return estoqueMaximo; }
    public BigDecimal getSaldoEstoque() { return saldoEstoque; }
    public String getLocalizacao() { return localizacao; }
    public boolean isAtivo() { return ativo; }
    public long getVersion() { return version; }
}
