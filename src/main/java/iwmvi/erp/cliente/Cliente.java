package iwmvi.erp.cliente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "clientes",
        uniqueConstraints = @UniqueConstraint(name = "uk_cliente_documento", columnNames = "documento"))
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    private TipoPessoa tipoPessoa;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    private String email;
    private String telefone;
    private String logradouro;
    private String numero;
    private String cidade;

    @Column(length = 2)
    private String estado;

    private String cep;

    @Column(nullable = false)
    private boolean ativo = true;

    protected Cliente() {}

    public Cliente(ClienteRequest request) {
        atualizar(request);
    }

    public void atualizar(ClienteRequest request) {
        this.tipoPessoa = request.tipoPessoa();
        this.nome = request.nome().trim();
        this.documento = request.documento().trim();
        this.email = request.email();
        this.telefone = request.telefone();
        this.logradouro = request.logradouro();
        this.numero = request.numero();
        this.cidade = request.cidade();
        this.estado = request.estado();
        this.cep = request.cep();
    }

    public void alternarAtivo() {
        this.ativo = !this.ativo;
    }

    public Long getId() {
        return id;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public String getNome() {
        return nome;
    }

    public String getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getCep() {
        return cep;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
