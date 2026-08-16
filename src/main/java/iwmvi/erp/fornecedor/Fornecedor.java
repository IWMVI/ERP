package iwmvi.erp.fornecedor;

import iwmvi.erp.shared.validation.DocumentoValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "fornecedores",
        uniqueConstraints =
                @UniqueConstraint(name = "uk_fornecedor_documento", columnNames = "documento"))
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    private String email;
    private String telefone;
    private String celular;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false)
    private boolean ativo = true;

    protected Fornecedor() {}

    public Fornecedor(FornecedorRequest request) {
        atualizar(request);
    }

    public void atualizar(FornecedorRequest request) {
        this.nome = request.nome().trim();
        this.nomeFantasia = request.nomeFantasia();
        this.documento = DocumentoValidator.somenteDigitos(request.documento());
        this.email = request.email();
        this.telefone = request.telefone();
        this.celular = request.celular();
        this.cep = DocumentoValidator.somenteDigitos(request.cep());
        this.logradouro = request.logradouro();
        this.numero = request.numero();
        this.complemento = request.complemento();
        this.bairro = request.bairro();
        this.cidade = request.cidade();
        this.estado = request.estado();
        this.observacoes = request.observacoes();
    }

    public void alternarAtivo() { this.ativo = !this.ativo; }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getNomeFantasia() { return nomeFantasia; }
    public String getDocumento() { return documento; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getCelular() { return celular; }
    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getObservacoes() { return observacoes; }
    public boolean isAtivo() { return ativo; }
}
