package iwmvi.erp.produto;

import iwmvi.erp.shared.validation.DocumentoValidator;
import jakarta.persistence.*;

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
    private boolean ativo = true;

    @Version
    private long version;

    protected Produto() {
    }

    public Produto(ProdutoRequest request) {
        atualizar(request);
    }

    public void atualizar(ProdutoRequest request) {
        this.codigo = request.codigo().trim();
        this.gtin =
            request.gtin() == null || request.gtin().isBlank()
                ? null
                : DocumentoValidator.somenteDigitos(request.gtin());
        this.nome = request.nome().trim();
        this.descricao = textoOpcional(request.descricao());
        this.marca = textoOpcional(request.marca());
        this.categoria = textoOpcional(request.categoria());
        this.subcategoria = textoOpcional(request.subcategoria());
        this.tipo = request.tipo();
        this.unidadeMedida = request.unidadeMedida();
        this.precoVenda = request.precoVenda();
        this.custo = request.custo();
        this.estoqueMinimo = request.estoqueMinimo();
        this.estoqueMaximo = request.estoqueMaximo();
        this.localizacao = textoOpcional(request.localizacao());
        this.controlaEstoque = request.controlaEstoque();
        this.ncm = digitosOpcionais(request.ncm());
        this.cest = digitosOpcionais(request.cest());
        this.origem = request.origem();
        this.unidadeTributavel =
            request.unidadeTributavel() == null ? request.unidadeMedida() : request.unidadeTributavel();
        this.fatorConversaoTributavel = request.fatorConversaoTributavel();
        this.tipoItemSped = request.tipoItemSped();
        this.pesoLiquido = request.pesoLiquido();
        this.pesoBruto = request.pesoBruto();
        this.largura = request.largura();
        this.altura = request.altura();
        this.comprimento = request.comprimento();
        this.volumes = request.volumes();
        this.prazoPreparacaoDias = request.prazoPreparacaoDias();
    }

    private String textoOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private String digitosOpcionais(String valor) {
        return valor == null || valor.isBlank() ? null : DocumentoValidator.somenteDigitos(valor);
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

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getGtin() {
        return gtin;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getMarca() {
        return marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public TipoProduto getTipo() {
        return tipo;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public BigDecimal getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public BigDecimal getEstoqueMaximo() {
        return estoqueMaximo;
    }

    public BigDecimal getSaldoEstoque() {
        return saldoEstoque;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public boolean isControlaEstoque() {
        return controlaEstoque;
    }

    public String getNcm() {
        return ncm;
    }

    public String getCest() {
        return cest;
    }

    public OrigemMercadoria getOrigem() {
        return origem;
    }

    public UnidadeMedida getUnidadeTributavel() {
        return unidadeTributavel;
    }

    public BigDecimal getFatorConversaoTributavel() {
        return fatorConversaoTributavel;
    }

    public TipoItemSped getTipoItemSped() {
        return tipoItemSped;
    }

    public BigDecimal getPesoLiquido() {
        return pesoLiquido;
    }

    public BigDecimal getPesoBruto() {
        return pesoBruto;
    }

    public BigDecimal getLargura() {
        return largura;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public BigDecimal getComprimento() {
        return comprimento;
    }

    public Integer getVolumes() {
        return volumes;
    }

    public Integer getPrazoPreparacaoDias() {
        return prazoPreparacaoDias;
    }

    public String getFotoArquivo() {
        return fotoArquivo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public long getVersion() {
        return version;
    }
}
