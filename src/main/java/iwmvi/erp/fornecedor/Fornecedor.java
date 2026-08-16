package iwmvi.erp.fornecedor;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedores", uniqueConstraints = @UniqueConstraint(name = "uk_fornecedor_documento", columnNames = "documento"))
public class Fornecedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String nome;
    @Column(nullable = false, unique = true, length = 20) private String documento;
    private String email;
    private String telefone;
    private String logradouro;
    private String numero;
    private String cidade;
    @Column(length = 2) private String estado;
    private String cep;
    @Column(nullable = false) private boolean ativo = true;

    protected Fornecedor() {}
    public Fornecedor(FornecedorRequest r) { atualizar(r); }
    public void atualizar(FornecedorRequest r) { nome=r.nome().trim(); documento=r.documento().trim(); email=r.email(); telefone=r.telefone(); logradouro=r.logradouro(); numero=r.numero(); cidade=r.cidade(); estado=r.estado(); cep=r.cep(); }
    public void alternarAtivo() { ativo=!ativo; }
    public Long getId(){return id;} public String getNome(){return nome;} public String getDocumento(){return documento;} public String getEmail(){return email;} public String getTelefone(){return telefone;} public String getLogradouro(){return logradouro;} public String getNumero(){return numero;} public String getCidade(){return cidade;} public String getEstado(){return estado;} public String getCep(){return cep;} public boolean isAtivo(){return ativo;}
}
