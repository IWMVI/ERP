package iwmvi.erp.produto;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="produtos",uniqueConstraints=@UniqueConstraint(name="uk_produto_codigo",columnNames="codigo"))
public class Produto {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=80) private String codigo;
    @Column(nullable=false) private String nome;
    private String categoria;
    @Enumerated(EnumType.STRING) @Column(name="unidade_medida",nullable=false) private UnidadeMedida unidadeMedida;
    @Column(name="preco_venda",nullable=false,precision=19,scale=2) private BigDecimal precoVenda;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal custo;
    @Column(name="estoque_minimo",nullable=false,precision=19,scale=3) private BigDecimal estoqueMinimo=BigDecimal.ZERO;
    @Column(name="saldo_estoque",nullable=false,precision=19,scale=3) private BigDecimal saldoEstoque=BigDecimal.ZERO;
    @Column(nullable=false) private boolean ativo=true;
    @Version private long version;
    protected Produto(){}
    public Produto(ProdutoRequest r){atualizar(r);}
    public void atualizar(ProdutoRequest r){codigo=r.codigo().trim();nome=r.nome().trim();categoria=r.categoria();unidadeMedida=r.unidadeMedida();precoVenda=r.precoVenda();custo=r.custo();estoqueMinimo=r.estoqueMinimo();}
    public void alternarAtivo(){ativo=!ativo;}
    public void definirSaldo(BigDecimal saldo){saldoEstoque=saldo;}
    public Long getId(){return id;} public String getCodigo(){return codigo;} public String getNome(){return nome;} public String getCategoria(){return categoria;} public UnidadeMedida getUnidadeMedida(){return unidadeMedida;} public BigDecimal getPrecoVenda(){return precoVenda;} public BigDecimal getCusto(){return custo;} public BigDecimal getEstoqueMinimo(){return estoqueMinimo;} public BigDecimal getSaldoEstoque(){return saldoEstoque;} public boolean isAtivo(){return ativo;} public long getVersion(){return version;}
}
